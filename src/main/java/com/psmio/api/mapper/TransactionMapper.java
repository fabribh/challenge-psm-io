package com.psmio.api.mapper;

import com.psmio.api.model.TransactionDTO;
import com.psmio.domain.exceptions.OperationTypeNotFoundException;
import com.psmio.domain.model.Account;
import com.psmio.domain.model.OperationType;
import com.psmio.domain.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class TransactionMapper implements Function<TransactionDTO, Transaction> {
    @Override
    public Transaction apply(TransactionDTO transactionDTO) {

        var id = transactionDTO.operationTypeId();
        OperationType operationType;

        try {
            operationType = OperationType.getById(id);
        } catch (IllegalArgumentException e) {
            throw new OperationTypeNotFoundException(id);
        }

        return Transaction.builder()
                .account(Account.builder()
                        .id(transactionDTO.accountId())
                        .build()
                )
                .operationType(operationType)
                .amount(transactionDTO.amount())
                .build();
    }
}
