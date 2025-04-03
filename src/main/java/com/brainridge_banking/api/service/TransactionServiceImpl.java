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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public TransactionResponse transferFunds(TransferRequest request) {
        // Validate request
        if (request.getFromAccountId() == null || request.getToAccountId() == null) {
            throw new IllegalArgumentException("Source and destination account IDs cannot be null");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new IllegalArgumentException("Source and destination accounts cannot be the same");
        }

        // Get accounts
        Account fromAccount = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found with id: " + request.getFromAccountId()));
        Account toAccount = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination account not found with id: " + request.getToAccountId()));

        // Check if sufficient funds
        if (fromAccount.getAccountBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account: " + request.getFromAccountId());
        }

        // Update account balances
        fromAccount.setAccountBalance(fromAccount.getAccountBalance().subtract(request.getAmount()));
        toAccount.setAccountBalance(toAccount.getAccountBalance().add(request.getAmount()));

        // Save updated accounts
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Create and save transaction
        Transaction transaction = new Transaction(
                fromAccount.getAccountId(),
                toAccount.getAccountId(),
                request.getAmount(),
                TransactionType.TRANSFER
        );
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Convert to response
        return mapToTransactionResponse(savedTransaction);
    }

    @Override
    public TransactionResponse deposit(TransactionRequest request) {
        // Validate request
        if (request.getToAccountId() == null) {
            throw new IllegalArgumentException("Account ID cannot be null");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        // Find account and verify it exists
        Account account = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + request.getToAccountId()));

        // Update balance
        account.setAccountBalance(account.getAccountBalance().add(request.getAmount()));
        accountRepository.save(account);

        // Create transaction record
        Transaction transaction = new Transaction(
                null, // No source account for deposit
                account.getAccountId(),
                request.getAmount(),
                TransactionType.DEPOSIT
        );
        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToTransactionResponse(savedTransaction);
    }

    @Override
    public TransactionResponse withdraw(TransactionRequest request) {
        // Validate request
        if (request.getToAccountId() == null) {
            throw new IllegalArgumentException("Account ID cannot be null");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        // Find account and verify it exists
        Account account = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + request.getToAccountId()));

        // Check for sufficient funds
        if (account.getAccountBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account: " + request.getToAccountId() +
                    ". Current balance: " + account.getAccountBalance() + ", requested amount: " + request.getAmount());
        }

        // Update balance
        account.setAccountBalance(account.getAccountBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        // Create transaction record
        Transaction transaction = new Transaction(
                account.getAccountId(),
                null, // No destination account for withdrawal
                request.getAmount(),
                TransactionType.WITHDRAWAL
        );
        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToTransactionResponse(savedTransaction);
    }

    @Override
    public List<TransactionResponse> getTransactionHistory(UUID accountId) {
        // Check if account exists
        if (accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Account not found with id: " + accountId);
        }

        // Get transactions and convert to response DTOs
        return transactionRepository.findByAccountId(accountId).stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    // Helper method to map Transaction entity to TransactionResponse DTO
    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setFromAccountId(transaction.getFromAccountId());
        response.setToAccountId(transaction.getToAccountId());
        response.setAmount(transaction.getAmount());
        response.setTimestamp(transaction.getTimestamp());
        response.setType(transaction.getType());
        return response;
    }
}