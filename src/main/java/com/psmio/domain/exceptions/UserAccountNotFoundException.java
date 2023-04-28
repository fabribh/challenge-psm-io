package com.psmio.domain.exceptions;

public class UserAccountNotFoundException extends BusinessException {

    public UserAccountNotFoundException(String message) {
        super(message);
    }

    public UserAccountNotFoundException(Long accountId) {
        this(String.format("Account not found to an account_id: %d", accountId));
    }
}
