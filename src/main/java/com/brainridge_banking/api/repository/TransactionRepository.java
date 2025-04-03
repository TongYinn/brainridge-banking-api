package com.brainridge_banking.api.repository;

import com.brainridge_banking.api.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class TransactionRepository {
    private final Map<UUID, Transaction> transactions = new ConcurrentHashMap<>();

    public Transaction save(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    public List<Transaction> findByAccountId(UUID accountId) {
        return transactions.values().stream()
                .filter(transaction ->
                        accountId.equals(transaction.getFromAccountId()) ||
                                accountId.equals(transaction.getToAccountId()))
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
}