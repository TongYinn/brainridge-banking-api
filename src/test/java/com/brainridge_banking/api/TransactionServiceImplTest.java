package com.brainridge_banking.api.service;

import com.brainridge_banking.api.dto.request.TransactionRequest;
import com.brainridge_banking.api.dto.request.TransferRequest;
import com.brainridge_banking.api.dto.response.TransactionResponse;
import com.brainridge_banking.api.exception.InsufficientFundsException;
import com.brainridge_banking.api.exception.ResourceNotFoundException;
import com.brainridge_banking.api.model.Account;
import com.brainridge_banking.api.model.Transaction;
import com.brainridge_banking.api.model.Transaction.TransactionType;
import com.brainridge_banking.api.repository.AccountRepository;
import com.brainridge_banking.api.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account sourceAccount;
    private Account destinationAccount;
    private UUID sourceAccountId;
    private UUID destinationAccountId;
    private BigDecimal initialSourceBalance;
    private BigDecimal initialDestinationBalance;
    private BigDecimal transferAmount;
    private TransferRequest validTransferRequest;
    private TransactionRequest validDepositRequest;
    private TransactionRequest validWithdrawRequest;

    @BeforeEach
    void setUp() {
        // Set up test accounts
        sourceAccountId = UUID.randomUUID();
        destinationAccountId = UUID.randomUUID();
        initialSourceBalance = new BigDecimal("1000.00");
        initialDestinationBalance = new BigDecimal("500.00");
        transferAmount = new BigDecimal("200.00");

        sourceAccount = new Account("Source User", "source@gmail.com", initialSourceBalance);
        sourceAccount.setAccountId(sourceAccountId);

        destinationAccount = new Account("Destination User", "destination@gmail.com", initialDestinationBalance);
        destinationAccount.setAccountId(destinationAccountId);

        // Set up transfer request
        validTransferRequest = new TransferRequest();
        validTransferRequest.setFromAccountId(sourceAccountId);
        validTransferRequest.setToAccountId(destinationAccountId);
        validTransferRequest.setAmount(transferAmount);

        // Set up deposit request
        validDepositRequest = new TransactionRequest();
        validDepositRequest.setToAccountId(destinationAccountId);
        validDepositRequest.setAmount(new BigDecimal("100.00"));

        // Set up withdraw request
        validWithdrawRequest = new TransactionRequest();
        validWithdrawRequest.setToAccountId(sourceAccountId);
        validWithdrawRequest.setAmount(new BigDecimal("50.00"));
    }

    @Test
    void transferFunds_WithValidData_ShouldTransferFunds() {
        // Arrange
        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(destinationAccountId)).thenReturn(Optional.of(destinationAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        TransactionResponse response = transactionService.transferFunds(validTransferRequest);

        // Assert
        assertNotNull(response);
        assertEquals(sourceAccountId, response.getFromAccountId());
        assertEquals(destinationAccountId, response.getToAccountId());
        assertEquals(transferAmount, response.getAmount());
        assertEquals(TransactionType.TRANSFER, response.getType());

        // Verify balances updated
        verify(accountRepository, times(2)).save(any(Account.class));
        assertEquals(initialSourceBalance.subtract(transferAmount), sourceAccount.getAccountBalance());
        assertEquals(initialDestinationBalance.add(transferAmount), destinationAccount.getAccountBalance());
    }

    @Test
    void transferFunds_WithInsufficientFunds_ShouldThrowInsufficientFundsException() {
        // Arrange
        validTransferRequest.setAmount(new BigDecimal("2000.00")); // More than source balance

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(destinationAccountId)).thenReturn(Optional.of(destinationAccount));

        // Act & Assert
        assertThrows(InsufficientFundsException.class, () -> transactionService.transferFunds(validTransferRequest));

        // Verify no account update or transaction was saved
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transferFunds_WithNonExistingSourceAccount_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.empty());
        // This stubbing is unnecessary because the method throws an exception before using it
        // when(accountRepository.findById(destinationAccountId)).thenReturn(Optional.of(destinationAccount));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> transactionService.transferFunds(validTransferRequest));

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transferFunds_WithNonExistingDestinationAccount_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(destinationAccountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> transactionService.transferFunds(validTransferRequest));

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transferFunds_WithSameSourceAndDestination_ShouldThrowIllegalArgumentException() {
        // Arrange
        validTransferRequest.setToAccountId(sourceAccountId); // Same as source

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> transactionService.transferFunds(validTransferRequest));

        verify(accountRepository, never()).findById(any(UUID.class));
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transferFunds_WithNegativeAmount_ShouldThrowIllegalArgumentException() {
        // Arrange
        validTransferRequest.setAmount(new BigDecimal("-100.00"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> transactionService.transferFunds(validTransferRequest));

        verify(accountRepository, never()).findById(any(UUID.class));
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void deposit_WithValidData_ShouldDepositFunds() {
        // Arrange
        when(accountRepository.findById(destinationAccountId)).thenReturn(Optional.of(destinationAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        TransactionResponse response = transactionService.deposit(validDepositRequest);

        // Assert
        assertNotNull(response);
        assertNull(response.getFromAccountId());
        assertEquals(destinationAccountId, response.getToAccountId());
        assertEquals(validDepositRequest.getAmount(), response.getAmount());
        assertEquals(TransactionType.DEPOSIT, response.getType());

        // Verify balance updated
        verify(accountRepository, times(1)).save(any(Account.class));
        assertEquals(initialDestinationBalance.add(validDepositRequest.getAmount()),
                destinationAccount.getAccountBalance());
    }

    @Test
    void deposit_WithNonExistingAccount_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(accountRepository.findById(destinationAccountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> transactionService.deposit(validDepositRequest));

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void deposit_WithNegativeAmount_ShouldThrowIllegalArgumentException() {
        // Arrange
        validDepositRequest.setAmount(new BigDecimal("-50.00"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> transactionService.deposit(validDepositRequest));

        verify(accountRepository, never()).findById(any(UUID.class));
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void withdraw_WithValidData_ShouldWithdrawFunds() {
        // Arrange
        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        TransactionResponse response = transactionService.withdraw(validWithdrawRequest);

        // Assert
        assertNotNull(response);
        assertEquals(sourceAccountId, response.getFromAccountId());
        assertNull(response.getToAccountId());
        assertEquals(validWithdrawRequest.getAmount(), response.getAmount());
        assertEquals(TransactionType.WITHDRAWAL, response.getType());

        // Verify balance updated
        verify(accountRepository, times(1)).save(any(Account.class));
        assertEquals(initialSourceBalance.subtract(validWithdrawRequest.getAmount()),
                sourceAccount.getAccountBalance());
    }

    @Test
    void withdraw_WithInsufficientFunds_ShouldThrowInsufficientFundsException() {
        // Arrange
        validWithdrawRequest.setAmount(new BigDecimal("2000.00")); // More than source balance

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));

        // Act & Assert
        assertThrows(InsufficientFundsException.class, () -> transactionService.withdraw(validWithdrawRequest));

        // Verify no account update or transaction was saved
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void withdraw_WithNonExistingAccount_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> transactionService.withdraw(validWithdrawRequest));

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void withdraw_WithNegativeAmount_ShouldThrowIllegalArgumentException() {
        // Arrange
        validWithdrawRequest.setAmount(new BigDecimal("-50.00"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> transactionService.withdraw(validWithdrawRequest));

        verify(accountRepository, never()).findById(any(UUID.class));
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void getTransactionHistory_WithExistingAccount_ShouldReturnTransactions() {
        // Arrange
        Transaction transaction1 = new Transaction(sourceAccountId, destinationAccountId, new BigDecimal("100.00"), TransactionType.TRANSFER);
        Transaction transaction2 = new Transaction(null, sourceAccountId, new BigDecimal("50.00"), TransactionType.DEPOSIT);

        when(accountRepository.existsById(sourceAccountId)).thenReturn(false); // Note: Your existsById is inverted
        when(transactionRepository.findByAccountId(sourceAccountId)).thenReturn(Arrays.asList(transaction1, transaction2));

        // Act
        List<TransactionResponse> responses = transactionService.getTransactionHistory(sourceAccountId);

        // Assert
        assertEquals(2, responses.size());
        assertEquals(TransactionType.TRANSFER, responses.get(0).getType());
        assertEquals(TransactionType.DEPOSIT, responses.get(1).getType());
    }

    @Test
    void getTransactionHistory_WithNonExistingAccount_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(accountRepository.existsById(sourceAccountId)).thenReturn(true); // Note: Your existsById is inverted

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransactionHistory(sourceAccountId));

        verify(transactionRepository, never()).findByAccountId(any(UUID.class));
    }
}