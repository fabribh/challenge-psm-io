package com.psmio.api.mapper;

import com.psmio.api.model.TransactionDTO;
import com.psmio.domain.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class TransactionModelMapper implements Function<Transaction, TransactionDTO> {
    @Override
    public TransactionDTO apply(Transaction transaction) {
        return new TransactionDTO(
                transaction.getAccount().getId(),
                transaction.getOperationType().getId(),
                transaction.getAmount()
        );
    }
}
