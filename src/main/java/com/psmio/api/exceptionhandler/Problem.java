package com.psmio.api.exceptionhandler;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
public class Problem {
    private String message;
    private Integer statusCode;
    private List<Field> fields;
    private OffsetDateTime timestamp;

    @Getter
    @Builder
    public static class Field{
        private String name;
        private String message;
    }
}