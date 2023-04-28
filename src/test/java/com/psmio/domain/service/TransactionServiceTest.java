package com.psmio.domain.service;

import com.psmio.domain.exceptions.UserAccountNotFoundException;
import com.psmio.domain.model.Account;
import com.psmio.domain.model.OperationType;
import com.psmio.domain.model.Transaction;
import com.psmio.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class TransactionServiceTest {

    @Mock
    TransactionRepository repository;

    @Mock
    AccountService accountService;

    private TransactionService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new TransactionService(repository, accountService);
    }

    @Test
    void testCreateTransactionWithBuyTheCashTypeAndNegativeAmount() {

        var amount = new BigDecimal("123.45");
        var account = createAccount();
        var transaction = createTransaction(amount, account, OperationType.BUY_THE_CASH);

        when(accountService.getAccountsByIdOrElseThrow(transaction.getAccount().getId()))
                .thenReturn(account);
        when(repository.save(any(Transaction.class)))
                .thenReturn(transaction);

        var transactionCreated = service.createTransaction(transaction);
        assertEquals(transaction.getTransaction_id(), transactionCreated.getTransaction_id());
        assertEquals(transaction.getAccount(), transactionCreated.getAccount());
        assertEquals(transaction.getOperationType(), transactionCreated.getOperationType());
        assertEquals(amount.negate(), transactionCreated.getAmount());
    }

    @Test
    void testCreateTransactionWithInstallmentPurchaseTypeAndNegativeAmount() {

        var amount = new BigDecimal("123.45");
        var account = createAccount();
        var transaction = createTransaction(amount, account, OperationType.INSTALLMENT_PURCHASE);

        when(accountService.getAccountsByIdOrElseThrow(transaction.getAccount().getId()))
                .thenReturn(account);
        when(repository.save(any(Transaction.class)))
                .thenReturn(transaction);

        var transactionCreated = service.createTransaction(transaction);
        assertEquals(transaction.getTransaction_id(), transactionCreated.getTransaction_id());
        assertEquals(transaction.getAccount(), transactionCreated.getAccount());
        assertEquals(transaction.getOperationType(), transactionCreated.getOperationType());
        assertEquals(amount.negate(), transactionCreated.getAmount());
    }

    @Test
    void testCreateTransactionWithWithdrawTypeAndNegativeAmount() {

        var amount = new BigDecimal("123.45");
        var account = createAccount();
        var transaction = createTransaction(amount, account, OperationType.WITHDRAW);

        when(accountService.getAccountsByIdOrElseThrow(transaction.getAccount().getId()))
                .thenReturn(account);
        when(repository.save(any(Transaction.class)))
                .thenReturn(transaction);

        var transactionCreated = service.createTransaction(transaction);
        assertEquals(transaction.getTransaction_id(), transactionCreated.getTransaction_id());
        assertEquals(transaction.getAccount(), transactionCreated.getAccount());
        assertEquals(transaction.getOperationType(), transactionCreated.getOperationType());
        assertEquals(amount.negate(), transactionCreated.getAmount());
    }

    @Test
    void testCreateTransactionWithPaymentTypeAndPositiveAmount() {

        var account = createAccount();
        var amount = new BigDecimal("123.45");
        var transaction = createTransaction(amount, account, OperationType.PAYMENT);

        when(accountService.getAccountsByIdOrElseThrow(transaction.getAccount().getId()))
                 .thenReturn(account);
        when(repository.save(any(Transaction.class)))
                 .thenReturn(transaction);

        var transactionCreated = service.createTransaction(transaction);
        assertEquals(transaction.getTransaction_id(), transactionCreated.getTransaction_id());
        assertEquals(transaction.getAccount(), transactionCreated.getAccount());
        assertEquals(transaction.getOperationType(), transactionCreated.getOperationType());
        assertEquals(amount, transactionCreated.getAmount());
    }

    @Test
    void testCreateTransactionWithNonexistentAccount() {

        var accountIdNonexistent = 1000L;
        var account = Account.builder().id(accountIdNonexistent).build();
        var transaction = createTransaction(new BigDecimal("1.08"), account, OperationType.PAYMENT);

        when(accountService.getAccountsByIdOrElseThrow(anyLong()))
                .thenThrow(new UserAccountNotFoundException(accountIdNonexistent));

        var exception = assertThrows(UserAccountNotFoundException.class,
                () -> service.createTransaction(transaction));
        assertEquals("Account not found to an account_id: " + accountIdNonexistent,
                exception.getMessage());
    }

    private static Transaction createTransaction(BigDecimal amount, Account account, OperationType type) {
        return Transaction.builder()
                .transaction_id(1L)
                .account(account)
                .operationType(type)
                .amount(amount)
                .build();
    }

    private static Account createAccount() {
        return Account.builder()
                .id(1L)
                .documentNumber("12345678900")
                .build();
    }
}