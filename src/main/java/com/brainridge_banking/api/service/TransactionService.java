package com.brainridge_banking.api.service;

import com.brainridge_banking.api.dto.request.TransactionRequest;
import com.brainridge_banking.api.dto.request.TransferRequest;
import com.brainridge_banking.api.dto.response.TransactionResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionService {
    TransactionResponse transferFunds(TransferRequest request);
    TransactionResponse deposit(TransactionRequest request);
    TransactionResponse withdraw(TransactionRequest request);
    List<TransactionResponse> getTransactionHistory(UUID accountId);
}