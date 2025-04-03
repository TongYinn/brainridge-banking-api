package com.brainridge_banking.api.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public class TransferRequest {

    private UUID fromAccountId;
    private UUID toAccountId;
    private BigDecimal amount;

    // Getters
    public UUID getFromAccountId() {
        return fromAccountId;
    }

    public UUID getToAccountId() {
        return toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    // Setters
    public void setFromAccountId(UUID fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public void setToAccountId(UUID toAccountId) {
        this.toAccountId = toAccountId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
