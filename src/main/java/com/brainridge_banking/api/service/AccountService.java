package com.brainridge_banking.api.service;

import com.brainridge_banking.api.dto.request.AccountCreationRequest;
import com.brainridge_banking.api.dto.request.AccountUpdateRequest;
import com.brainridge_banking.api.dto.response.AccountResponse;
import com.brainridge_banking.api.dto.response.AccountBalanceResponse;
import com.brainridge_banking.api.exception.ResourceNotFoundException;
import com.brainridge_banking.api.model.Account;
import com.brainridge_banking.api.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface AccountService {
    AccountResponse createAccount(AccountCreationRequest request);
    AccountResponse getAccountById(UUID id);
    List<AccountResponse> getAllAccounts();
    AccountResponse updateAccount(UUID id, AccountUpdateRequest request);
    void deleteAccount(UUID id);
    AccountBalanceResponse getAccountBalance(UUID id);
}