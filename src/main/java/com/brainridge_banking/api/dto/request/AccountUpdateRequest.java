package com.brainridge_banking.api.dto.request;

public class AccountUpdateRequest {
    private String accountName;
    private String accountEmail;

    // Getters
    public String getAccountName() {
        return accountName;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    // Setters
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }
}
