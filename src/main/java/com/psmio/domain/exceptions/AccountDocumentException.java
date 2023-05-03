package com.psmio.domain.exceptions;

public class AccountDocumentException extends BusinessException{
    public AccountDocumentException(String message) {
        super(String.format("%s", message));
    }
}
