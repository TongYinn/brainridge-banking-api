package com.brainridge_banking.api;

import com.brainridge_banking.api.controller.TransactionController;
import com.brainridge_banking.api.dto.request.TransactionRequest;
import com.brainridge_banking.api.dto.request.TransferRequest;
import com.brainridge_banking.api.dto.response.TransactionResponse;
import com.brainridge_banking.api.exception.InsufficientFundsException;
import com.brainridge_banking.api.exception.ResourceNotFoundException;
import com.brainridge_banking.api.model.Transaction.TransactionType;
import com.brainridge_banking.api.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {
    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private UUID sourceAccountId;
    private UUID destinationAccountId;
    private UUID transactionId;
    private TransferRequest transferRequest;
    private TransactionRequest depositRequest;
    private TransactionRequest withdrawRequest;
    private TransactionResponse transactionResponse;

    @BeforeEach
    void setUp() {
        // Initialize test data
        sourceAccountId = UUID.randomUUID();
        destinationAccountId = UUID.randomUUID();
        transactionId = UUID.randomUUID();

        transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(sourceAccountId);
        transferRequest.setToAccountId(destinationAccountId);
        transferRequest.setAmount(new BigDecimal("100.00"));

        depositRequest = new TransactionRequest();
        depositRequest.setToAccountId(destinationAccountId);
        depositRequest.setAmount(new BigDecimal("50.00"));

        withdrawRequest = new TransactionRequest();
        withdrawRequest.setToAccountId(sourceAccountId);
        withdrawRequest.setAmount(new BigDecimal("25.00"));

        transactionResponse = new TransactionResponse();
        transactionResponse.setId(transactionId);
        transactionResponse.setFromAccountId(sourceAccountId);
        transactionResponse.setToAccountId(destinationAccountId);
        transactionResponse.setAmount(new BigDecimal("100.00"));
        transactionResponse.setTimestamp(LocalDateTime.now());
        transactionResponse.setType(TransactionType.TRANSFER);
    }

    @Test
    void transferFunds_WithValidData_ShouldReturnCreatedStatus() {
        // Arrange
        when(transactionService.transferFunds(any(TransferRequest.class))).thenReturn(transactionResponse);

        // Act
        ResponseEntity<TransactionResponse> response = transactionController.transferFunds(transferRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(transactionId, response.getBody().getId());
        verify(transactionService, times(1)).transferFunds(transferRequest);
    }

    @Test
    void transferFunds_WithInsufficientFunds_ShouldThrowInsufficientFundsException() {
        // Arrange
        when(transactionService.transferFunds(any(TransferRequest.class)))
                .thenThrow(new InsufficientFundsException("Insufficient funds"));

        // Act & Assert
        assertThrows(InsufficientFundsException.class, () ->
                transactionController.transferFunds(transferRequest));
        verify(transactionService, times(1)).transferFunds(transferRequest);
    }

    @Test
    void transferFunds_WithNonExistingAccount_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(transactionService.transferFunds(any(TransferRequest.class)))
                .thenThrow(new ResourceNotFoundException("Account not found"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                transactionController.transferFunds(transferRequest));
        verify(transactionService, times(1)).transferFunds(transferRequest);
    }

    @Test
    void deposit_WithValidData_ShouldReturnCreatedStatus() {
        // Arrange
        TransactionResponse depositResponse = new TransactionResponse();
        depositResponse.setId(UUID.randomUUID());
        depositResponse.setToAccountId(destinationAccountId);
        depositResponse.setAmount(new BigDecimal("50.00"));
        depositResponse.setType(TransactionType.DEPOSIT);

        when(transactionService.deposit(any(TransactionRequest.class))).thenReturn(depositResponse);

        // Act
        ResponseEntity<TransactionResponse> response = transactionController.deposit(depositRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(destinationAccountId, response.getBody().getToAccountId());
        assertEquals(TransactionType.DEPOSIT, response.getBody().getType());
        verify(transactionService, times(1)).deposit(depositRequest);
    }

    @Test
    void deposit_WithNonExistingAccount_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(transactionService.deposit(any(TransactionRequest.class)))
                .thenThrow(new ResourceNotFoundException("Account not found"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                transactionController.deposit(depositRequest));
        verify(transactionService, times(1)).deposit(depositRequest);
    }

    @Test
    void withdraw_WithValidData_ShouldReturnCreatedStatus() {
        // Arrange
        TransactionResponse withdrawResponse = new TransactionResponse();
        withdrawResponse.setId(UUID.randomUUID());
        withdrawResponse.setFromAccountId(sourceAccountId);
        withdrawResponse.setAmount(new BigDecimal("25.00"));
        withdrawResponse.setType(TransactionType.WITHDRAWAL);

        when(transactionService.withdraw(any(TransactionRequest.class))).thenReturn(withdrawResponse);

        // Act
        ResponseEntity<TransactionResponse> response = transactionController.withdraw(withdrawRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sourceAccountId, response.getBody().getFromAccountId());
        assertEquals(TransactionType.WITHDRAWAL, response.getBody().getType());
        verify(transactionService, times(1)).withdraw(withdrawRequest);
    }

    @Test
    void withdraw_WithInsufficientFunds_ShouldThrowInsufficientFundsException() {
        // Arrange
        when(transactionService.withdraw(any(TransactionRequest.class)))
                .thenThrow(new InsufficientFundsException("Insufficient funds"));

        // Act & Assert
        assertThrows(InsufficientFundsException.class, () ->
                transactionController.withdraw(withdrawRequest));
        verify(transactionService, times(1)).withdraw(withdrawRequest);
    }

    @Test
    void withdraw_WithNonExistingAccount_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(transactionService.withdraw(any(TransactionRequest.class)))
                .thenThrow(new ResourceNotFoundException("Account not found"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                transactionController.withdraw(withdrawRequest));
        verify(transactionService, times(1)).withdraw(withdrawRequest);
    }

    @Test
    void getTransactionHistory_WithExistingAccount_ShouldReturnTransactions() {
        // Arrange
        TransactionResponse transaction1 = new TransactionResponse();
        transaction1.setId(UUID.randomUUID());
        transaction1.setType(TransactionType.DEPOSIT);

        TransactionResponse transaction2 = new TransactionResponse();
        transaction2.setId(UUID.randomUUID());
        transaction2.setType(TransactionType.WITHDRAWAL);

        List<TransactionResponse> transactions = Arrays.asList(transaction1, transaction2);

        when(transactionService.getTransactionHistory(sourceAccountId)).thenReturn(transactions);

        // Act
        ResponseEntity<List<TransactionResponse>> response = transactionController.getTransactionHistory(sourceAccountId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(transactionService, times(1)).getTransactionHistory(sourceAccountId);
    }

    @Test
    void getTransactionHistory_WithNonExistingAccount_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(transactionService.getTransactionHistory(sourceAccountId))
                .thenThrow(new ResourceNotFoundException("Account not found"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                transactionController.getTransactionHistory(sourceAccountId));
        verify(transactionService, times(1)).getTransactionHistory(sourceAccountId);
    }
}
