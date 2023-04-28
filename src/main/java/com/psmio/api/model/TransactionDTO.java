package com.psmio.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record TransactionDTO(
        @JsonProperty("account_id")
        Long accountId,
        @JsonProperty("operation_type_id")
        Integer operationTypeId,
        BigDecimal amount
) {}
