package com.brainridge_banking.api.service;

import com.brainridge_banking.api.dto.request.AccountCreationRequest;
import com.brainridge_banking.api.dto.request.AccountUpdateRequest;
import com.brainridge_banking.api.dto.response.AccountBalanceResponse;
import com.brainridge_banking.api.dto.response.AccountResponse;
import com.brainridge_banking.api.exception.DuplicateEmailException;
import com.brainridge_banking.api.exception.InvalidEmailException;
import com.brainridge_banking.api.exception.ResourceNotFoundException;
import com.brainridge_banking.api.model.Account;
import com.brainridge_banking.api.repository.AccountRepository;
import com.brainridge_banking.api.util.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountResponse createAccount(AccountCreationRequest request) {
        // Validate request
        if (request.getAccountName() == null || request.getAccountName().trim().isEmpty()) {
            throw new IllegalArgumentException("Account name cannot be empty");
        }
        if (request.getAccountEmail() == null || request.getAccountEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Account email cannot be empty");
        }
        if (request.getInitialBalance() == null || request.getInitialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance must be non-negative");
        }

        // Validate email format and domain
        if (!EmailValidator.isValidEmail(request.getAccountEmail())) {
            throw new InvalidEmailException(
                    EmailValidator.getInvalidEmailMessage(request.getAccountEmail()));
        }

        // Check if email already exists
        boolean emailExists = accountRepository.findAll().stream()
                .anyMatch(account -> request.getAccountEmail().equals(account.getAccountEmail()));

        if (emailExists) {
            throw new DuplicateEmailException("An account with this email already exists: " + request.getAccountEmail());
        }

        // Create and save account
        Account account = new Account(
                request.getAccountName(),
                request.getAccountEmail(),
                request.getInitialBalance()
        );
        Account savedAccount = accountRepository.save(account);

        // Convert to response
        return mapToAccountResponse(savedAccount);
    }

    @Override
    public AccountResponse getAccountById(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        return mapToAccountResponse(account);
    }

    @Override
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::mapToAccountResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponse updateAccount(UUID id, AccountUpdateRequest request) {
        // Find the account
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        // Validate request
        if (request.getAccountName() != null && !request.getAccountName().trim().isEmpty()) {
            account.setAccountName(request.getAccountName());
        }

        if (request.getAccountEmail() != null && !request.getAccountEmail().trim().isEmpty()) {
            // Validate new email format
            if (!EmailValidator.isValidEmail(request.getAccountEmail())) {
                throw new InvalidEmailException(
                        EmailValidator.getInvalidEmailMessage(request.getAccountEmail()));
            }

            // Check if new email already exists (if it's different from current)
            if (!request.getAccountEmail().equalsIgnoreCase(account.getAccountEmail())) {
                boolean emailExists = accountRepository.findAll().stream()
                        .anyMatch(a -> !a.getAccountId().equals(id) &&
                                request.getAccountEmail().equalsIgnoreCase(a.getAccountEmail()));

                if (emailExists) {
                    throw new DuplicateEmailException("An account with this email already exists: " + request.getAccountEmail());
                }

                account.setAccountEmail(request.getAccountEmail());
            }
        }

        // Save updated account
        Account updatedAccount = accountRepository.save(account);

        // Convert to response
        return mapToAccountResponse(updatedAccount);
    }

    @Override
    public void deleteAccount(UUID id) {
        // Check if account exists
        if (accountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Account not found with id: " + id);
        }

        // Delete account
        accountRepository.deleteById(id);
    }

    @Override
    public AccountBalanceResponse getAccountBalance(UUID id) {
        // Find the account
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        // Create balance response
        AccountBalanceResponse response = new AccountBalanceResponse();
        response.setAccountId(account.getAccountId());
        response.setBalance(account.getAccountBalance());

        return response;
    }

    // Helper method to map Account entity to AccountResponse DTO
    private AccountResponse mapToAccountResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setAccountId(account.getAccountId());
        response.setAccountName(account.getAccountName());
        response.setAccountEmail(account.getAccountEmail());
        response.setAccountBalance(account.getAccountBalance());
        response.setCreatedAt(account.getCreatedAt());
        return response;
    }
}