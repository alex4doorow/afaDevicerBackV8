package com.afa.devicer.back.entities.products;

import com.afa.core.enums.StockTypes;
import com.afa.core.enums.SupplierTypes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bp_stock_supplier_products",
        indexes = {
                @Index(name = "idx_bp_stock_supplier_products_stock", columnList = "stock"),
                @Index(name = "idx_bp_stock_supplier_products_supplier", columnList = "supplier"),
                @Index(name = "idx_bp_stock_supplier_products_product_id", columnList = "product_id")
        })
@SuppressWarnings({"PMD.TooManyFields", "PMD.AvoidDuplicateLiterals", "PMD.LawOfDemeter"})
public class StockSupplierProduct {

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Product product;

    @NotNull
    @Builder.Default
    @Column(name = "supplier_price", nullable = false)
    private BigDecimal supplierPrice = BigDecimal.ZERO;

    @NotNull
    @Builder.Default
    @Column(name = "supplier_quantity", nullable = false)
    private Integer supplierQuantity = 0;

    @NotNull
    @Builder.Default
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    @Column(name = "comment")
    private String comment;
}
