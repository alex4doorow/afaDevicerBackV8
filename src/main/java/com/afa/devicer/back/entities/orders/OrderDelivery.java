package com.afa.devicer.back.entities.orders;

import com.afa.devicer.back.entities.dictionaries.Address;
import com.afa.devicer.back.entities.people.Person;
import com.afa.devicer.back.enums.DeliveryPriceTypes;
import com.afa.devicer.back.enums.DeliveryTypes;
import com.afa.devicer.back.enums.PaymentDeliveryTypes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Order order;

    /**
     * Значение, которое ввел оператор
     */
    @NotNull
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    /**
     * Сколько платит покупатель
     */
    @NotNull
    @Column(name = "customer_price", nullable = false)
    private BigDecimal factCustomerPrice;

    /**
     * Сколько платит продавец
     */
    @NotNull
    @Column(name = "seller_price", nullable = false)
    private BigDecimal factSellerPrice;

    @NotNull
    @Column(name = "delivery_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryTypes deliveryType;

    @NotNull
    @Column(name = "delivery_price_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryPriceTypes deliveryPrice;

    @NotNull
    @Column(name = "payment_delivery_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentDeliveryTypes paymentDeliveryType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    @ToString.Exclude
    @JsonIgnore
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    @ToString.Exclude
    @JsonIgnore
    private Person recipient;

//    @NotNull
//    @Column(name = "carrier_status", nullable = false)
//    @Enumerated(EnumType.STRING)
//    private CarrierStatuses carrierStatus;

    @Column(name = "annotation")
    private String annotation;

    @Column(name = "track_code")
    private String trackCode;

//    date_delivery` datetime DEFAULT NULL,
//            `time_in` time DEFAULT NULL,
//
//            `time_out` time DEFAULT NULL,

    @NotNull
    @Column(name = "rec_status", nullable = false)
    private Character recStatus;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_added", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Person userAdded;

    @NotNull
    @Column(name = "date_added", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateAdded = Instant.now();

    @Column(name = "date_modified", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateModified;
}
