package com.afa.devicer.back.entities.orders;

import com.afa.core.enums.*;
import com.afa.core.utils.DefaultConstants;
import com.afa.devicer.back.entities.customers.Customer;
import com.afa.devicer.back.entities.people.Person;
import com.afa.devicer.back.entities.products.ProductCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_orders",
        indexes = {
                @Index(name = "uq_bp_orders_order_num", columnList = "order_num", unique = true),
                @Index(name = "indx_bp_orders_order_type", columnList = "order_type"),
                @Index(name = "indx_bp_orders_customer_id", columnList = "customer_id")
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

    @NotNull
    @Builder.Default
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @NotNull
    @Builder.Default
    @Column(name = "total_with_delivery_amount", nullable = false)
    private BigDecimal totalWithDeliveryAmount = BigDecimal.ZERO;

    @NotNull
    @Builder.Default
    @Column(name = "bill_amount", nullable = false)
    private BigDecimal billAmount = BigDecimal.ZERO;

    @NotNull
    @Builder.Default
    @Column(name = "supplier_amount", nullable = false)
    private BigDecimal supplierAmount = BigDecimal.ZERO;

    @NotNull
    @Builder.Default
    @Column(name = "margin_amount", nullable = false)
    private BigDecimal marginAmount = BigDecimal.ZERO;

    @NotNull
    @Builder.Default
    @Column(name = "postpay_amount", nullable = false)
    private BigDecimal postpayAmount = BigDecimal.ZERO;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private Set<OrderItem> items;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private Set<OrderCrm> crms;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, optional = false)
    private OrderDelivery delivery;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private Set<OrderStatusHistory> statusHistory = new HashSet<>();

    @NotNull
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatusTypes status;

    @Column(name = "email_status")
    @Enumerated(EnumType.STRING)
    private OrderEmailStatusTypes emailStatus;

    @Column(name = "offer_count_day", nullable = false)
    private Integer offerCountDay;

    @Column(name = "offer_start_date")
    private LocalDate offerStartDate;

    @Column(name = "annotation", nullable = false)
    private String annotation;

    @NotNull
    @Builder.Default
    @Column(name = "rec_status", nullable = false)
    private Character recStatus = DefaultConstants.ACTIVE;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_added", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Person userAdded;

    @NotNull
    @Builder.Default
    @Column(name = "date_added", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateAdded = Instant.now();

    @Column(name = "date_modified", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateModified;

    @PreUpdate
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void preUpdate() {
        this.dateModified = Instant.now();
    }

    public CrmTypes getExternalCrm() {
        final CrmTypes result;
        if (this.getExternalCrmByCode(CrmTypes.OZON) != null) {
            result = CrmTypes.OZON;
        } else if (this.getExternalCrmByCode(CrmTypes.YANDEX_MARKET) != null) {
            result = CrmTypes.YANDEX_MARKET;
        } else if (this.getExternalCrmByCode(CrmTypes.OPENCART) != null) {
            result = CrmTypes.OPENCART;
        } else {
            result = CrmTypes.NONE;
        }
        return result;
    }

    public OrderCrm getExternalCrmByCode(final CrmTypes crmTypes) {
        if (crms == null || crms.isEmpty() || crmTypes == CrmTypes.NONE) {
            return null;
        }
        for (final OrderCrm externalCrm : crms) {
            if (crmTypes == externalCrm.getCrm()) {
                return externalCrm;
            }
        }
        return null;
    }

    @SuppressWarnings("PMD.SimplifyBooleanReturns")
    public boolean isPrepayment() {
        if (paymentType == OrderPaymentTypes.PREPAYMENT || paymentType == OrderPaymentTypes.CARD_PAY) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings({"PMD.NcssCount", "PMD.CognitiveComplexity", "PMD.CyclomaticComplexity"})
    public boolean isBillAmount() {
        if (this.getType() == OrderTypes.ORDER) {
            if (this.getStatus() == OrderStatusTypes.APPROVED) {
                return true;
            } else if (this.getStatus() == OrderStatusTypes.PAY_WAITING) {
                return true;
            } else if (this.getStatus() == OrderStatusTypes.PAY_ON) {
                return true;
            } else if (this.getStatus() == OrderStatusTypes.DELIVERING) {
                return true;
            } else if (this.getStatus() == OrderStatusTypes.READY_GIVE_AWAY) {
                return true;
            } else if (this.getStatus() == OrderStatusTypes.READY_GIVE_AWAY_TROUBLE) {
                return true;
            } else if (this.getStatus() == OrderStatusTypes.DELIVERED) {
                return true;
            } else if (this.getStatus() == OrderStatusTypes.FINISHED) {
                return true;
            } else if (this.getStatus() == OrderStatusTypes.DOC_NOT_EXIST) {
                return true;
            }
        } else if (this.getType() == OrderTypes.BILL) {
            if (this.isPrepayment()) {
                if (this.getStatus() == OrderStatusTypes.APPROVED) {
                    return false;
                } else if (this.getStatus() == OrderStatusTypes.PAY_WAITING) {
                    return false;
                } else if (this.getStatus() == OrderStatusTypes.PAY_ON) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.DELIVERING) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.READY_GIVE_AWAY) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.READY_GIVE_AWAY_TROUBLE) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.DELIVERED) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.FINISHED) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.DOC_NOT_EXIST) {
                    return true;
                }
            } else if (this.getPaymentType() == OrderPaymentTypes.POSTPAY) {

                if (this.getStatus() == OrderStatusTypes.APPROVED) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.PAY_WAITING) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.PAY_ON) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.DELIVERING) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.READY_GIVE_AWAY) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.READY_GIVE_AWAY_TROUBLE) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.DELIVERED) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.FINISHED) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.DOC_NOT_EXIST) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.CyclomaticComplexity"})
    public boolean isPostpayAmount() {

        if (this.getType() == OrderTypes.ORDER) {
            if (this.getPaymentType() == OrderPaymentTypes.POSTPAY) {
                // заказ ФЛ с наложенным платежом
                if (this.getStatus() == OrderStatusTypes.APPROVED) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.DELIVERING) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.READY_GIVE_AWAY) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.READY_GIVE_AWAY_TROUBLE) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.DELIVERED) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.REDELIVERY) {
                    return true;
                }
            } else if (this.getPaymentType() == OrderPaymentTypes.PAYMENT_COURIER) {
                return false;
            }
        } else if (this.getType() == OrderTypes.BILL) {
            if (this.isPrepayment()) {
                return false;
            } else if (this.getPaymentType() == OrderPaymentTypes.POSTPAY) {
                if (this.getStatus() == OrderStatusTypes.APPROVED) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.DELIVERING) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.READY_GIVE_AWAY) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.READY_GIVE_AWAY_TROUBLE) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.DELIVERED) {
                    return true;
                } else if (this.getStatus() == OrderStatusTypes.PAY_WAITING) {
                    return true;
                }
            }
        }
        return false;
    }
}
