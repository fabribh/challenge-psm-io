package com.psmio.api.exceptionhandler;

import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public record ApiBodyException(String message, Integer statusCode, HttpStatus status, OffsetDateTime timestamp) {

}
