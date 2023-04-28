package com.psmio.domain.service;

import com.psmio.domain.exceptions.UserAccountNotFoundException;
import com.psmio.domain.model.Account;
import com.psmio.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account addAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account getAccountsByIdOrElseThrow(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new UserAccountNotFoundException(accountId));
    }
}
