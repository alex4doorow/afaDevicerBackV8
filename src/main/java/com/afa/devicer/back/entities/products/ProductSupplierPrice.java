package com.afa.devicer.back.entities.products;

import com.afa.core.enums.DeliveryPaymentMethods;
import com.afa.core.enums.StockTypes;
import com.afa.core.enums.SupplierTypes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bp_product_supplier_prices",
        indexes = {
                @Index(name = "idx_bp_product_supplier_prices_stock", columnList = "stock"),
                @Index(name = "idx_bp_product_supplier_prices_supplier", columnList = "supplier"),
                @Index(name = "idx_bp_product_supplier_prices_product_id", columnList = "product_id", unique = true),
        })
@SuppressWarnings({"PMD.TooManyFields", "PMD.AvoidDuplicateLiterals", "PMD.LawOfDemeter"})
public class ProductSupplierPrice {

    @Id
    @NotNull
    @SequenceGenerator(name = "d_sequence", sequenceName = "d_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @Column(name = "stock", nullable = false)
    @Enumerated(EnumType.STRING)
    private StockTypes stock;

    @NotNull
    @Column(name = "supplier", nullable = false)
    @Enumerated(EnumType.STRING)
    private SupplierTypes supplier;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    @ToString.Exclude
    @JsonIgnore
    private Product product;

    @NotNull
    @Column(name = "supplier_price", nullable = false)
    private BigDecimal supplierPrice = BigDecimal.ZERO;

    /**
     * остаток на складе поставщика - обновляем по фиду
     */
    @NotNull
    @Column(name = "supplier_quantity", nullable = false)
    private Integer supplierQuantity = 0;

    /**
     * остаток на нашем складе
     */
    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    @NotNull
    @Column(name = "delivery_payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryPaymentMethods deliveryPaymentMethod;

    @Column(name = "comment")
    private String comment;
}
