package com.psmio.domain.exceptions;

import java.math.BigDecimal;

public class IllegalTransactionException extends BusinessException{
    public IllegalTransactionException(String message) {
        super(message);
    }

    public IllegalTransactionException(String message, BigDecimal amount) {
        this(String.format("%s %d", message, amount));
    }
}
