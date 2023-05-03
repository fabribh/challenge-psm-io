package com.psmio.domain.service;

import com.psmio.domain.exceptions.IllegalTransactionException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        var amount = new BigDecimal("5000");
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
        assertEquals(account.getAvailableCreditLimit(), BigDecimal.ZERO);
    }

    @Test
    void testCreateTransactionWithInstallmentPurchaseTypeAndNegativeAmountAlsoUpdateTheLimitOfAccount() {

        var amount = new BigDecimal("4999.99");
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
        assertEquals(account.getAvailableCreditLimit(), BigDecimal.valueOf(0.01));
    }

    @Test
    void testCreateTransactionWithWithdrawTypeAndNegativeAmountAlsoUpdateTheLimitOfAccount() {

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
        assertEquals(account.getAvailableCreditLimit(), BigDecimal.valueOf(4876.55));
    }

    @Test
    void testCreateTransactionWithPaymentTypeAndPositiveAmountAlsoUpdateLimitOfAccount() {

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
        assertEquals(account.getAvailableCreditLimit(), BigDecimal.valueOf(5123.45));
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

    @Test
    void testCreateTransactionWithAmountValueZero() {

        var account = createAccount();
        var amount = BigDecimal.ZERO;
        var transaction = createTransaction(BigDecimal.ZERO, account, OperationType.PAYMENT);

        when(accountService.getAccountsByIdOrElseThrow(transaction.getAccount().getId()))
                .thenReturn(account);

        var exception = assertThrows(IllegalTransactionException.class,
                () -> service.createTransaction(transaction));
        assertEquals(TransactionService.MUST_BE_GREATER_THAN_0.concat(amount.toString()),
                exception.getMessage());
    }

    @Test
    void testCreateTransactionBuyTheCashWithAmountGreaterThanLimitOfAccount() {

        var account = createAccount();
        var amount = new BigDecimal("5000.01");
        var transaction = createTransaction(amount, account, OperationType.BUY_THE_CASH);

        when(accountService.getAccountsByIdOrElseThrow(transaction.getAccount().getId()))
                .thenReturn(account);
        var exception = assertThrows(IllegalTransactionException.class,
                () -> service.createTransaction(transaction));
        assertEquals(TransactionService.LIMIT_IS_NOT_ENOUGH.concat(amount.toString()),
                exception.getMessage());
    }

    @Test
    void testCreateTransactionInstallmentPurchaseWithAmountGreaterThanLimitOfAccount() {

        var account = createAccount();
        var amount = new BigDecimal("5000.02");
        var transaction = createTransaction(amount, account, OperationType.INSTALLMENT_PURCHASE);

        when(accountService.getAccountsByIdOrElseThrow(transaction.getAccount().getId()))
                .thenReturn(account);
        var exception = assertThrows(IllegalTransactionException.class,
                () -> service.createTransaction(transaction));
        assertEquals(TransactionService.LIMIT_IS_NOT_ENOUGH.concat(amount.toString()),
                exception.getMessage());
    }

    @Test
    void testCreateTransactionWithdrawWithAmountGreaterThanLimitOfAccount() {

        var account = createAccount();
        var amount = new BigDecimal("5000.001");
        var transaction = createTransaction(amount, account, OperationType.WITHDRAW);

        when(accountService.getAccountsByIdOrElseThrow(transaction.getAccount().getId()))
                .thenReturn(account);
        var exception = assertThrows(IllegalTransactionException.class,
                () -> service.createTransaction(transaction));
        assertEquals(TransactionService.LIMIT_IS_NOT_ENOUGH.concat(amount.toString()),
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
                .availableCreditLimit(new BigDecimal("5000"))
                .build();
    }
}