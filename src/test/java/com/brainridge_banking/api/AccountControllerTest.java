package com.brainridge_banking.api;

import com.brainridge_banking.api.controller.AccountController;
import com.brainridge_banking.api.dto.request.AccountCreationRequest;
import com.brainridge_banking.api.dto.request.AccountUpdateRequest;
import com.brainridge_banking.api.dto.response.AccountBalanceResponse;
import com.brainridge_banking.api.dto.response.AccountResponse;
import com.brainridge_banking.api.exception.DuplicateEmailException;
import com.brainridge_banking.api.exception.InvalidEmailException;
import com.brainridge_banking.api.exception.ResourceNotFoundException;
import com.brainridge_banking.api.service.AccountService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private UUID testAccountId;
    private AccountResponse testAccountResponse;
    private AccountCreationRequest creationRequest;
    private AccountUpdateRequest updateRequest;
    private AccountBalanceResponse balanceResponse;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testAccountId = UUID.randomUUID();

        testAccountResponse = new AccountResponse();
        testAccountResponse.setAccountId(testAccountId);
        testAccountResponse.setAccountName("Test User");
        testAccountResponse.setAccountEmail("test@gmail.com");
        testAccountResponse.setAccountBalance(new BigDecimal("1000.00"));
        testAccountResponse.setCreatedAt(LocalDateTime.now());

        creationRequest = new AccountCreationRequest();
        creationRequest.setAccountName("New User");
        creationRequest.setAccountEmail("new.user@gmail.com");
        creationRequest.setInitialBalance(new BigDecimal("500.00"));

        updateRequest = new AccountUpdateRequest();
        updateRequest.setAccountName("Updated Name");
        updateRequest.setAccountEmail("updated@gmail.com");

        balanceResponse = new AccountBalanceResponse();
        balanceResponse.setAccountId(testAccountId);
        balanceResponse.setBalance(new BigDecimal("1000.00"));
    }

    @Test
    void createAccount_ShouldReturnCreatedStatusAndAccountResponse() {
        // Arrange
        when(accountService.createAccount(any(AccountCreationRequest.class))).thenReturn(testAccountResponse);

        // Act
        ResponseEntity<AccountResponse> response = accountController.createAccount(creationRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testAccountId, response.getBody().getAccountId());
        verify(accountService, times(1)).createAccount(creationRequest);
    }

    @Test
    void getAccount_WithExistingId_ShouldReturnAccountResponse() {
        // Arrange
        when(accountService.getAccountById(testAccountId)).thenReturn(testAccountResponse);

        // Act
        ResponseEntity<AccountResponse> response = accountController.getAccount(testAccountId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testAccountId, response.getBody().getAccountId());
        verify(accountService, times(1)).getAccountById(testAccountId);
    }

    @Test
    void getAccount_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(accountService.getAccountById(testAccountId)).thenThrow(new ResourceNotFoundException("Account not found"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> accountController.getAccount(testAccountId));
        verify(accountService, times(1)).getAccountById(testAccountId);
    }

    @Test
    void getAllAccounts_ShouldReturnAllAccounts() {
        // Arrange
        AccountResponse account1 = new AccountResponse();
        account1.setAccountId(UUID.randomUUID());
        account1.setAccountName("User 1");

        AccountResponse account2 = new AccountResponse();
        account2.setAccountId(UUID.randomUUID());
        account2.setAccountName("User 2");

        List<AccountResponse> accounts = Arrays.asList(account1, account2);

        when(accountService.getAllAccounts()).thenReturn(accounts);

        // Act
        ResponseEntity<List<AccountResponse>> response = accountController.getAllAccounts();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(accountService, times(1)).getAllAccounts();
    }

    @Test
    void updateAccount_WithValidData_ShouldReturnUpdatedAccount() {
        // Arrange
        when(accountService.updateAccount(eq(testAccountId), any(AccountUpdateRequest.class)))
                .thenReturn(testAccountResponse);

        // Act
        ResponseEntity<AccountResponse> response = accountController.updateAccount(testAccountId, updateRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testAccountId, response.getBody().getAccountId());
        verify(accountService, times(1)).updateAccount(testAccountId, updateRequest);
    }

    @Test
    void updateAccount_WithInvalidEmail_ShouldThrowInvalidEmailException() {
        // Arrange
        when(accountService.updateAccount(eq(testAccountId), any(AccountUpdateRequest.class)))
                .thenThrow(new InvalidEmailException("Invalid email"));

        // Act & Assert
        assertThrows(InvalidEmailException.class, () ->
                accountController.updateAccount(testAccountId, updateRequest));
        verify(accountService, times(1)).updateAccount(testAccountId, updateRequest);
    }

    @Test
    void updateAccount_WithDuplicateEmail_ShouldThrowDuplicateEmailException() {
        // Arrange
        when(accountService.updateAccount(eq(testAccountId), any(AccountUpdateRequest.class)))
                .thenThrow(new DuplicateEmailException("Duplicate email"));

        // Act & Assert
        assertThrows(DuplicateEmailException.class, () ->
                accountController.updateAccount(testAccountId, updateRequest));
        verify(accountService, times(1)).updateAccount(testAccountId, updateRequest);
    }

    @Test
    void deleteAccount_WithExistingId_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(accountService).deleteAccount(testAccountId);

        // Act
        ResponseEntity<Void> response = accountController.deleteAccount(testAccountId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(accountService, times(1)).deleteAccount(testAccountId);
    }

    @Test
    void deleteAccount_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Arrange
        doThrow(new ResourceNotFoundException("Account not found"))
                .when(accountService).deleteAccount(testAccountId);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> accountController.deleteAccount(testAccountId));
        verify(accountService, times(1)).deleteAccount(testAccountId);
    }

    @Test
    void getAccountBalance_WithExistingId_ShouldReturnBalance() {
        // Arrange
        when(accountService.getAccountBalance(testAccountId)).thenReturn(balanceResponse);

        // Act
        ResponseEntity<AccountBalanceResponse> response = accountController.getAccountBalance(testAccountId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testAccountId, response.getBody().getAccountId());
        assertEquals(new BigDecimal("1000.00"), response.getBody().getBalance());
        verify(accountService, times(1)).getAccountBalance(testAccountId);
    }

    @Test
    void getAccountBalance_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(accountService.getAccountBalance(testAccountId))
                .thenThrow(new ResourceNotFoundException("Account not found"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> accountController.getAccountBalance(testAccountId));
        verify(accountService, times(1)).getAccountBalance(testAccountId);
    }
}
