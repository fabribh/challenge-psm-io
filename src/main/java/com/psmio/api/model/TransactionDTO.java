package com.psmio.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionDTO(
        @Positive @JsonProperty("account_id") Long accountId,
        @Positive @JsonProperty("operation_type_id") Integer operationTypeId,
        @Positive @JsonProperty("amount") BigDecimal amount
) {}
