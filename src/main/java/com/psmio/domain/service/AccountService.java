package com.psmio.domain.service;

import com.psmio.domain.exceptions.AccountDocumentException;
import com.psmio.domain.exceptions.UserAccountNotFoundException;
import com.psmio.domain.model.Account;
import com.psmio.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AccountService {

    public static final String ALREADY_EXIST_ACCOUNT_WITH_THIS_DOCUMENT = "Already exist account with this document: ";
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account addAccount(Account account) {
        if (Objects.nonNull(accountRepository.findAccountByDocumentNumber(account.getDocumentNumber()))) {
            throw new AccountDocumentException(ALREADY_EXIST_ACCOUNT_WITH_THIS_DOCUMENT + account.getDocumentNumber());
        }
        return accountRepository.save(account);
    }

    public Account getAccountsByIdOrElseThrow(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new UserAccountNotFoundException(accountId));
    }
}
