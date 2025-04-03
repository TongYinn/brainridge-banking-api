package com.brainridge_banking.api.repository;

import com.brainridge_banking.api.model.Account;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountRepository{
    private final Map<UUID, Account> accounts = new ConcurrentHashMap<>();

    public Account save(Account account) {
        accounts.put(account.getAccountId(), account);
        return account;
    }

    public Optional<Account> findById(UUID id) {
        return Optional.ofNullable(accounts.get(id));
    }

    public boolean existsById(UUID id) {
        return !accounts.containsKey(id);
    }

    public List<Account> findAll() {
        return new ArrayList<>(accounts.values());
    }

    public void deleteById(UUID id) {
        accounts.remove(id);
    }
}