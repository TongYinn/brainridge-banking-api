package com.brainridge_banking.api.dto.request;

import java.math.BigDecimal;

public class AccountCreationRequest {

    private String accountName;
    private String accountEmail;
    private BigDecimal initialBalance;

    // Getters
    public String getAccountName() {
        return accountName;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    // Setters
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

}