package com.psmio.api.exceptionhandler;

import com.psmio.domain.exceptions.BusinessException;
import com.psmio.domain.exceptions.IllegalTransactionException;
import com.psmio.domain.exceptions.OperationTypeNotFoundException;
import com.psmio.domain.exceptions.UserAccountNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {OperationTypeNotFoundException.class, UserAccountNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFound(BusinessException ex, WebRequest request) {

        var apiBodyException = new ApiBodyException(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND,
                OffsetDateTime.now()
        );

        return handleExceptionInternal(ex, apiBodyException, new HttpHeaders(),
                HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = IllegalTransactionException.class)
    public ResponseEntity<Object> handleTransactionOperationException(BusinessException ex, WebRequest request) {

        var apiBodyException = new ApiBodyException(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST,
                OffsetDateTime.now()
        );

        return handleExceptionInternal(ex, apiBodyException, new HttpHeaders(),
                HttpStatus.BAD_REQUEST, request);
    }
}
