package com.brainridge_banking.api;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.brainridge_banking.api.repository.AccountRepository;
import com.brainridge_banking.api.repository.TransactionRepository;
import com.brainridge_banking.api.service.AccountService;
import com.brainridge_banking.api.service.AccountServiceImpl;
import com.brainridge_banking.api.service.TransactionService;
import com.brainridge_banking.api.service.TransactionServiceImpl;

/**
 * Test configuration that can be used for integration tests
 * to provide real implementations of repositories and services.
 */
@TestConfiguration
public class TestConfig {
    @Bean
    @Primary
    public AccountRepository accountRepository() {
        return new AccountRepository();
    }

    @Bean
    @Primary
    public TransactionRepository transactionRepository() {
        return new TransactionRepository();
    }

    @Bean
    @Primary
    public AccountService accountService(AccountRepository accountRepository) {
        return new AccountServiceImpl(accountRepository);
    }

    @Bean
    @Primary
    public TransactionService transactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository) {
        return new TransactionServiceImpl(transactionRepository, accountRepository);
    }
}
