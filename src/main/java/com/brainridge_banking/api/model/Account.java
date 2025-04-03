package com.brainridge_banking.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.LocalDateTime;

public class Account {
    private UUID accountId;
    private String accountName;
    private String accountEmail;
    private BigDecimal accountBalance;
    private final LocalDateTime createdAt;

    public Account() {
        this.accountId = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
    }

    public Account(String accountName, String accountEmail, BigDecimal initialBalance) {
        this();
        this.accountName = accountName;
        this.accountEmail = accountEmail;
        this.accountBalance = initialBalance;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }
}
