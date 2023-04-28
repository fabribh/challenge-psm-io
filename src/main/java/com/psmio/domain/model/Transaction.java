package com.psmio.domain.model;

import com.psmio.domain.converter.OperationTypeConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transaction {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transaction_id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @NotNull
    private Account account;

    @NotNull
    @Convert(converter = OperationTypeConverter.class)
    private OperationType operationType;

    @Column(nullable = false)
    @NotNull
    private BigDecimal amount;

    @CreationTimestamp
    private LocalDateTime eventDate;
}
