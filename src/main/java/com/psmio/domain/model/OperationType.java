package com.psmio.domain.model;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum OperationType {
    BUY_THE_CASH(1, "COMPRA A VISTA", -1),
    INSTALLMENT_PURCHASE(2, "COMPRA PARCELADA", -1),
    WITHDRAW(3, "SAQUE", -1),
    PAYMENT(4, "PAGAMENTO", 1);

    private Integer id;
    private String value;
    private Integer multiplying;

    private OperationType(Integer id, String value, Integer multiplying) {
        this.id = id;
        this.value = value;
        this.multiplying = multiplying;
    }

    public static OperationType getById(Integer id) {
        return Stream.of(values())
                .filter(op -> op.id.equals(id))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
