package com.afa.devicer.back.entities.products;

import com.afa.core.enums.CrmTypes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bp_product_marketplaces",
        indexes = {
                @Index(name = "idx_bp_product_marketplaces_sku", columnList = "sku"),
                @Index(name = "idx_bp_product_marketplaces_product_id", columnList = "product_id")

        })
public class ProductMarketplace {

    @Id
    @NotNull
    @SequenceGenerator(name = "d_sequence", sequenceName = "d_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @ToString.Exclude
    @JsonIgnore
    private Product product;

    @NotNull
    @Column(name = "sku", nullable = false)
    private String sku;

    /**
     * От склада поставщика = true
     * <br/>
     * от нашего склада = false
     */
    @NotNull
    @Column(name = "supplier_stock", nullable = false)
    private boolean supplierStock;

    /**
     * Продажи на marketplace
     * <br/>
     * разрешены = true
     * <br/>
     * блокированы = false
     */
    @NotNull
    @Column(name = "marketplace_seller", nullable = false)
    private boolean marketSeller;

    @NotNull
    @Column(name = "marketplace_type", nullable = false)
    private CrmTypes marketType;

    @NotNull
    @Column(name = "special_price", nullable = false)
    private BigDecimal specialPrice;

    @NotNull
    @Column(name = "rec_status", nullable = false)
    private Character recStatus;

    @NotNull
    @Column(name = "date_added", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateAdded = Instant.now();

    @Column(name = "date_modified", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateModified;

    public static ProductMarketplace createEmpty(final CrmTypes marketplace) {
        return new ProductMarketplace(0L,
                null,
                "",
                false,
                false,
                marketplace,
                BigDecimal.ZERO,
                'A',
                Instant.now(),
                null);
    }
}
