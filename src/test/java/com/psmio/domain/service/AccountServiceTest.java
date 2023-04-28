package com.psmio.domain.service;

import com.psmio.domain.exceptions.UserAccountNotFoundException;
import com.psmio.domain.model.Account;
import com.psmio.domain.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class AccountServiceTest {

    @Mock
    AccountRepository accountRepository;

    private AccountService accountService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        accountService = new AccountService(accountRepository);
    }

    @Test
    void testAddAccount() {
        var account = createAccount();

        when(accountRepository.save(account))
                .thenReturn(account);

        var accountCreated = accountService.addAccount(account);
        assertEquals(account.getId(), accountCreated.getId());
        assertEquals(account.getDocumentNumber(), accountCreated.getDocumentNumber());
    }

    @Test
    void testGetAccountById() {
        var account = createAccount();

        when(accountRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(account));

        var accountFound = accountService.getAccountsByIdOrElseThrow(anyLong());
        assertEquals(account.getId(), accountFound.getId());
        assertEquals(account.getDocumentNumber(), accountFound.getDocumentNumber());
    }

    @Test
    void testGetAccountByIdNonexistent() {
        var nonexistentId = 10L;
        when(accountRepository.findById(nonexistentId))
                .thenThrow(new UserAccountNotFoundException(nonexistentId));

        var exception = assertThrows(UserAccountNotFoundException.class,
                () -> accountService.getAccountsByIdOrElseThrow(nonexistentId));
        assertEquals("Account not found to an account_id: " + nonexistentId, exception.getMessage());
    }

    private static Account createAccount() {
        return Account.builder()
                .id(1L)
                .documentNumber("12345678900")
                .build();
    }
}