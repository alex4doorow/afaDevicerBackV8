package com.afa.devicer.back.entities.orders;

import com.afa.devicer.back.entities.people.Person;
import com.afa.devicer.back.entities.products.Product;
import com.afa.core.utils.DefaultConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_order_items",
        indexes = {
                @Index(name = "uq_bp_order_items_order_id_item_num", columnList = "order_id, item_num", unique = true),
                @Index(name = "idx_bp_order_items_product_id", columnList = "product_id")
        })
public class OrderItem {

    @Id
    @NotNull
    @SequenceGenerator(name = "d_sequence", sequenceName = "d_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @Column(name = "item_num", nullable = false)
    private Integer itemNum;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Order order;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Product product;

    @NotNull
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @NotNull
    @Column(name = "supplier_price", nullable = false)
    private BigDecimal supplierPrice = BigDecimal.ZERO;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "discount_rate", nullable = false)
    private BigDecimal discountRate;

    @NotNull
    @Column(name = "amount", nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @NotNull
    @Column(name = "supplier_amount", nullable = false)
    private BigDecimal supplierAmount = BigDecimal.ZERO;

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
