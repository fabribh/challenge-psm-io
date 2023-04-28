package com.psmio.domain.exceptions;

public class OperationTypeNotFoundException extends BusinessException{
    public OperationTypeNotFoundException(String message) {
        super(message);
    }

    public OperationTypeNotFoundException(Integer operationTypeId) {
        this(String.format("Operation Type not found for an operation_type_id: %d", operationTypeId));
    }
}
