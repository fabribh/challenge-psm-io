package com.psmio.domain.service;

import com.psmio.domain.model.OperationType;
import com.psmio.domain.model.Transaction;
import com.psmio.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    public Transaction createTransaction(Transaction transaction) {
        var account = accountService.getAccountsByIdOrElseThrow(transaction.getAccount().getId());

        transaction.setAccount(account);
        transaction.setAmount(calculateAmount(transaction.getOperationType(), transaction.getAmount()));
        return transactionRepository.save(transaction);
    }

    private BigDecimal calculateAmount(OperationType type, BigDecimal amount) {
        return new BigDecimal(String.valueOf(amount.multiply(BigDecimal.valueOf(type.getMultiplying()))));
    }
}
