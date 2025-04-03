package com.brainridge_banking.api;

import com.brainridge_banking.api.dto.request.AccountCreationRequest;
import com.brainridge_banking.api.dto.request.AccountUpdateRequest;
import com.brainridge_banking.api.dto.response.AccountBalanceResponse;
import com.brainridge_banking.api.dto.response.AccountResponse;
import com.brainridge_banking.api.exception.DuplicateEmailException;
import com.brainridge_banking.api.exception.InvalidEmailException;
import com.brainridge_banking.api.exception.ResourceNotFoundException;
import com.brainridge_banking.api.model.Account;
import com.brainridge_banking.api.repository.AccountRepository;
import com.brainridge_banking.api.service.AccountServiceImpl;
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
public class AccountServiceImplTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account testAccount;
    private UUID testAccountId;
    private AccountCreationRequest validCreationRequest;
    private AccountUpdateRequest validUpdateRequest;

    @BeforeEach
    void setUp() {
        testAccountId = UUID.randomUUID();
        testAccount = new Account("Test User", "test@gmail.com", new BigDecimal("1000.00"));
        testAccount.setAccountId(testAccountId);

        validCreationRequest = new AccountCreationRequest();
        validCreationRequest.setAccountName("New User");
        validCreationRequest.setAccountEmail("new.user@gmail.com");
        validCreationRequest.setInitialBalance(new BigDecimal("500.00"));

        validUpdateRequest = new AccountUpdateRequest();
        validUpdateRequest.setAccountName("Updated Name");
        validUpdateRequest.setAccountEmail("updated@gmail.com");
    }

    @Test
    void createAccount_WithValidData_ShouldCreateAccount() {
        // Arrange
        when(accountRepository.findAll()).thenReturn(List.of());
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        AccountResponse response = accountService.createAccount(validCreationRequest);

        // Assert
        assertNotNull(response);
        assertEquals(validCreationRequest.getAccountName(), response.getAccountName());
        assertEquals(validCreationRequest.getAccountEmail(), response.getAccountEmail());
        assertEquals(validCreationRequest.getInitialBalance(), response.getAccountBalance());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void createAccount_WithInvalidEmail_ShouldThrowInvalidEmailException() {
        // Arrange
        validCreationRequest.setAccountEmail("invalid-email");

        // Act & Assert
        assertThrows(InvalidEmailException.class, () -> accountService.createAccount(validCreationRequest));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createAccount_WithDuplicateEmail_ShouldThrowDuplicateEmailException() {
        // Arrange
        Account existingAccount = new Account("Existing User", validCreationRequest.getAccountEmail(), new BigDecimal("1000.00"));
        when(accountRepository.findAll()).thenReturn(List.of(existingAccount));

        // Act & Assert
        assertThrows(DuplicateEmailException.class, () -> accountService.createAccount(validCreationRequest));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createAccount_WithNegativeBalance_ShouldThrowIllegalArgumentException() {
        // Arrange
        validCreationRequest.setInitialBalance(new BigDecimal("-100.00"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(validCreationRequest));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void getAccountById_WithExistingId_ShouldReturnAccount() {
        // Arrange
        when(accountRepository.findById(testAccountId)).thenReturn(Optional.of(testAccount));

        // Act
        AccountResponse response = accountService.getAccountById(testAccountId);

        // Assert
        assertNotNull(response);
        assertEquals(testAccount.getAccountId(), response.getAccountId());
        assertEquals(testAccount.getAccountName(), response.getAccountName());
        assertEquals(testAccount.getAccountEmail(), response.getAccountEmail());
        assertEquals(testAccount.getAccountBalance(), response.getAccountBalance());
    }

    @Test
    void getAccountById_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        when(accountRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountById(nonExistingId));
    }

    @Test
    void getAllAccounts_ShouldReturnAllAccounts() {
        // Arrange
        Account account1 = new Account("User 1", "user1@gmail.com", new BigDecimal("100.00"));
        Account account2 = new Account("User 2", "user2@gmail.com", new BigDecimal("200.00"));
        when(accountRepository.findAll()).thenReturn(Arrays.asList(account1, account2));

        // Act
        List<AccountResponse> responses = accountService.getAllAccounts();

        // Assert
        assertEquals(2, responses.size());
        assertEquals(account1.getAccountName(), responses.get(0).getAccountName());
        assertEquals(account2.getAccountName(), responses.get(1).getAccountName());
    }

    @Test
    void updateAccount_WithValidData_ShouldUpdateAccount() {
        // Arrange
        when(accountRepository.findById(testAccountId)).thenReturn(Optional.of(testAccount));
        when(accountRepository.findAll()).thenReturn(List.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        AccountResponse response = accountService.updateAccount(testAccountId, validUpdateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(validUpdateRequest.getAccountName(), response.getAccountName());
        assertEquals(validUpdateRequest.getAccountEmail(), response.getAccountEmail());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void updateAccount_WithInvalidEmail_ShouldThrowInvalidEmailException() {
        // Arrange
        when(accountRepository.findById(testAccountId)).thenReturn(Optional.of(testAccount));
        validUpdateRequest.setAccountEmail("invalid-email");

        // Act & Assert
        assertThrows(InvalidEmailException.class, () -> accountService.updateAccount(testAccountId, validUpdateRequest));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void updateAccount_WithDuplicateEmail_ShouldThrowDuplicateEmailException() {
        // Arrange
        Account existingAccount = new Account("Existing User", "existing@gmail.com", new BigDecimal("1000.00"));
        existingAccount.setAccountId(UUID.randomUUID()); // Different ID

        when(accountRepository.findById(testAccountId)).thenReturn(Optional.of(testAccount));
        when(accountRepository.findAll()).thenReturn(List.of(testAccount, existingAccount));

        validUpdateRequest.setAccountEmail("existing@gmail.com");

        // Act & Assert
        assertThrows(DuplicateEmailException.class, () -> accountService.updateAccount(testAccountId, validUpdateRequest));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void deleteAccount_WithExistingId_ShouldDeleteAccount() {
        // Arrange
        when(accountRepository.existsById(testAccountId)).thenReturn(false); // Note: Your existsById is inverted (returns false if id exists)

        // Act
        accountService.deleteAccount(testAccountId);

        // Assert
        verify(accountRepository, times(1)).deleteById(testAccountId);
    }

    @Test
    void deleteAccount_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        when(accountRepository.existsById(nonExistingId)).thenReturn(true); // Note: Your existsById is inverted

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> accountService.deleteAccount(nonExistingId));
        verify(accountRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void getAccountBalance_WithExistingId_ShouldReturnBalance() {
        // Arrange
        when(accountRepository.findById(testAccountId)).thenReturn(Optional.of(testAccount));

        // Act
        AccountBalanceResponse response = accountService.getAccountBalance(testAccountId);

        // Assert
        assertNotNull(response);
        assertEquals(testAccountId, response.getAccountId());
        assertEquals(testAccount.getAccountBalance(), response.getBalance());
    }

    @Test
    void getAccountBalance_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        when(accountRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountBalance(nonExistingId));
    }
}
