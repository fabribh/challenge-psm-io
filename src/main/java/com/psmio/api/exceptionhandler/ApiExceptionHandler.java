package com.psmio.api.exceptionhandler;

import com.psmio.domain.exceptions.BusinessException;
import com.psmio.domain.exceptions.OperationTypeNotFoundException;
import com.psmio.domain.exceptions.UserAccountNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {UserAccountNotFoundException.class, OperationTypeNotFoundException.class})
    public ResponseEntity<Object> handleUserAccountNotFound(BusinessException e) {

        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.NOT_FOUND,
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
    }
}
