package com.afa.devicer.back.entities.orders;

import com.afa.core.enums.AmountTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_period_total_amounts",
        indexes = {
                @Index(name = "indx_bp_period_total_amounts_amount_type_start_date_end_date", columnList = "amount_type, start_date, end_date")
        })
public class PeriodTotalAmount {

    @Id
    @NotNull
    @SequenceGenerator(name = "d_sequence", sequenceName = "d_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @Column(name = "amount_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AmountTypes amountType;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull
    @Builder.Default
    @Column(name = "amount", nullable = false)
    private BigDecimal amount =  BigDecimal.ZERO;
}
