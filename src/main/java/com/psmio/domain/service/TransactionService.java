package com.psmio.domain.service;

import com.psmio.domain.exceptions.IllegalTransactionException;
import com.psmio.domain.model.Account;
import com.psmio.domain.model.OperationType;
import com.psmio.domain.model.Transaction;
import com.psmio.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionService {

    public static final String MUST_BE_GREATER_THAN_0 = "must be greater than ";
    public static final String LIMIT_IS_NOT_ENOUGH = "Limit is not enough for transaction with value: ";
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        var account = accountService.getAccountsByIdOrElseThrow(transaction.getAccount().getId());
        var amountReceived = transaction.getAmount();

        validateLimitOfAnAccount(transaction, account);

        transaction.setAccount(account);
        var newAmount = calculateAmount(transaction.getOperationType(), amountReceived);
        transaction.setAmount(newAmount);

        updateAccountCreditLimit(account, newAmount);
        return transactionRepository.save(transaction);
    }

    private void updateAccountCreditLimit(Account account, BigDecimal newAmount) {
        account.setAvailableCreditLimit(account.getAvailableCreditLimit().add(newAmount));
    }

    private void validateLimitOfAnAccount(Transaction transaction, Account account) {
        if (transaction.getOperationType().getValue() != OperationType.PAYMENT.getValue())
            if (account.getAvailableCreditLimit().compareTo(transaction.getAmount()) == -1)
                throw new IllegalTransactionException(LIMIT_IS_NOT_ENOUGH + transaction.getAmount());
    }

    private BigDecimal calculateAmount(OperationType type, BigDecimal amount) {
        if (BigDecimal.ZERO.compareTo(amount) == 0)
            throw new IllegalTransactionException(MUST_BE_GREATER_THAN_0 + amount);

        return new BigDecimal(String.valueOf(amount.multiply(BigDecimal.valueOf(type.getMultiplying()))));
    }
}
