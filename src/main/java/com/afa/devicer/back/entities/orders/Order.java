package com.afa.devicer.back.entities.orders;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "bp_order",
        indexes = {
                @Index(name = "uq_bp_order", columnList = "order_num")
        })
@SuppressWarnings({"PMD.TooManyFields", "PMD.AvoidDuplicateLiterals", "PMD.LawOfDemeter"})
public class Order {

    @Id
    @NotNull
    @SequenceGenerator(name = "D_SEQUENCE", sequenceName = "D_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "D_SEQUENCE")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "order_num", nullable = false)
    private Long orderNum;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "rec_status", nullable = false)
    private Character recStatus;

//        @ManyToOne(fetch = FetchType.EAGER)
//        @JoinColumn(name = "user_added", referencedColumnName = "ID")
//        private SEUser userAdded;

    @NotNull
    @Column(name = "date_added", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateAdded = Instant.now();

    @Column(name = "date_modified", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateModified;
}
