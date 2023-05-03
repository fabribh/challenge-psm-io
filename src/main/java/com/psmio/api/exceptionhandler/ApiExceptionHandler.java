package com.psmio.api.exceptionhandler;

import com.psmio.domain.exceptions.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String ONE_OR_MORE_FIELDS_ARE_INVALID = "One or more fields are invalid!";

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

    @ExceptionHandler(value = {IllegalTransactionException.class, AccountDocumentException.class})
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

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        var detail = ONE_OR_MORE_FIELDS_ARE_INVALID;
        List<Problem.Field> problemProblems = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new Problem.Field.FieldBuilder()
                        .name(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());
        var apiBodyException = Problem.builder()
                .message(detail)
                .statusCode(ex.getStatusCode().value())
                .fields(problemProblems)
                .timestamp(OffsetDateTime.now())
                .build();

        return handleExceptionInternal(ex, apiBodyException, headers, status, request);
    }
}