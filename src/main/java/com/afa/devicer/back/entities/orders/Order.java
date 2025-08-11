package com.afa.devicer.back.entities.orders;

import com.afa.devicer.back.entities.customers.Customer;
import com.afa.devicer.back.entities.dictionaries.ProductCategory;
import com.afa.devicer.back.entities.people.Person;
import com.afa.devicer.back.enums.*;
import com.afa.devicer.back.utils.DefaultConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "bp_orders",
        indexes = {
                @Index(name = "uq_bp_orders_order_num", columnList = "order_num",  unique = true),
                @Index(name = "indx_bp_orders_customer_id", columnList = "customer_id"),
                @Index(name = "indx_bp_orders_order_type", columnList = "order_type")
        })
@SuppressWarnings({"PMD.TooManyFields", "PMD.AvoidDuplicateLiterals", "PMD.LawOfDemeter"})
public class Order {

    @Id
    @NotNull
    @SequenceGenerator(name = "d_sequence", sequenceName = "d_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "order_num", nullable = false)
    private Long orderNum;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @NotNull
    @Column(name = "order_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderTypes type;

    @NotNull
    @Column(name = "source_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderSourceTypes sourceType;

    @NotNull
    @Column(name = "advert_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderAdvertTypes advertType;

    @NotNull
    @Column(name = "payment_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderPaymentTypes paymentType;

    @NotNull
    @Column(name = "store", nullable = false)
    @Enumerated(EnumType.STRING)
    private StoreTypes store;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_category_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private ProductCategory productCategory;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Customer customer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private Set<OrderCrm> crms;

    @NotNull
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatusTypes status;

    @NotNull
    @Column(name = "rec_status", nullable = false)
    private Character recStatus = DefaultConstants.ACTIVE;

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
