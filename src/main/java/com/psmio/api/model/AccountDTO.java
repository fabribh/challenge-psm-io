package com.psmio.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AccountDTO(
        @NotBlank @JsonProperty("document_number") String documentNumber,
        @Positive @JsonProperty("available_credit_limit") BigDecimal availableCreditLimit
) {
}
