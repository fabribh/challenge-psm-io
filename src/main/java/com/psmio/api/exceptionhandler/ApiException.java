package com.psmio.api.exceptionhandler;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public record ApiException(String message, HttpStatus httpStatus, ZonedDateTime zonedDateTime) {

}
