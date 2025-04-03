package com.brainridge_banking.api.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountBalanceResponse {
    private UUID accountId;
    private BigDecimal balance;

    // Getters
    public UUID getAccountId() {
        return accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    // Setters
    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
