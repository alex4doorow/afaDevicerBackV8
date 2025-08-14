package com.afa.devicer.back.entities.orders;

import com.afa.devicer.back.entities.dictionaries.Address;
import com.afa.devicer.back.entities.people.Person;
import com.afa.devicer.back.enums.DeliveryPriceTypes;
import com.afa.devicer.back.enums.DeliveryTypes;
import com.afa.devicer.back.enums.DeliveryPaymentTypes;
import com.afa.devicer.back.utils.DefaultConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_order_deliveries",
        indexes = {
                @Index(name = "idx_bp_order_deliveries_order_id", columnList = "order_id"),
        })
@SuppressWarnings({"PMD.TooManyFields", "PMD.AvoidDuplicateLiterals", "PMD.LawOfDemeter"})
public class OrderDelivery {

    @Id
    @NotNull
    @SequenceGenerator(name = "d_sequence", sequenceName = "d_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @NotNull
    @Column(name = "delivery_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryTypes deliveryType;

    @NotNull
    @Column(name = "delivery_payment_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryPaymentTypes deliveryPaymentType;

    @Column(name = "delivery_price_type")
    @Enumerated(EnumType.STRING)
    private DeliveryPriceTypes deliveryPriceType;

    /**
     * Значение, которое ввел оператор
     */
    @NotNull
    @Builder.Default
    @Column(name = "price", nullable = false)
    private BigDecimal price = BigDecimal.ZERO;

    /**
     * Сколько платит покупатель - доставка за счет покупателя
     */
    @NotNull
    @Builder.Default
    @Column(name = "customer_price", nullable = false)
    private BigDecimal factCustomerPrice = BigDecimal.ZERO;

    /**
     * Сколько платит продавец - доставка за счет продавца
     */
    @NotNull
    @Builder.Default
    @Column(name = "seller_price", nullable = false)
    private BigDecimal factSellerPrice = BigDecimal.ZERO;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id",  nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Address address;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Person recipient;

    @Column(name = "annotation")
    private String annotation;

    @Column(name = "track_code")
    private String trackCode;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "time_in")
    private LocalTime timeIn;

    @Column(name = "time_out")
    private LocalTime timeOut;

    @NotNull
    @Builder.Default
    @Column(name = "rec_status", nullable = false)
    private Character recStatus = DefaultConstants.ACTIVE;

    @NotNull
    @Builder.Default
    @Column(name = "date_added", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateAdded = Instant.now();

    @Column(name = "date_modified", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateModified;
}
