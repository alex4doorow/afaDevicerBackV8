package com.afa.devicer.back.entities.products;

import com.afa.devicer.back.entities.dictionaries.Manufacture;
import com.afa.core.enums.LengthClasses;
import com.afa.core.enums.ProductTypes;
import com.afa.core.enums.StockStatusTypes;
import com.afa.core.enums.WeightClasses;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bp_products",
        indexes = {
                @Index(name = "idx_bp_products_sku", columnList = "sku"),
                @Index(name = "idx_bp_products_product_type", columnList = "product_type")
        })
@SuppressWarnings({"PMD.TooManyFields", "PMD.AvoidDuplicateLiterals", "PMD.LawOfDemeter"})
public class Product {

    @Id
    @NotNull
    @SequenceGenerator(name = "d_sequence", sequenceName = "d_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @Column(name = "product_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductTypes type;

    @NotNull
    @Column(name = "sku", nullable = false)
    private String sku;

    @NotNull
    @Column(name = "short_name", nullable = false)
    private String shortName;

    @Column(name = "long_name")
    private String longName;

    @Column(name = "image_url")
    private String imageUrl;

    @NotNull
    @Column(name = "quantity")
    private Integer quantity;

    @NotNull
    @Column(name = "minimum")
    private Integer minimum;

    @NotNull
    @Column(name = "price")
    private BigDecimal price;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_category_id")
    @ToString.Exclude
    @JsonIgnore
    private ProductCategory category;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacture_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Manufacture manufacture;

    @NotNull
    @Column(name = "stock_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StockStatusTypes stockStatus;

    @NotNull
    @Column(name = "date_available", nullable = false)
    private LocalDate dateAvailable;

    @Column(name = "weight")
    private BigDecimal weight;

    @Column(name = "weight_class", nullable = false)
    @Enumerated(EnumType.STRING)
    private WeightClasses weightClass;

    @Column(name = "length")
    private BigDecimal length;

    @Column(name = "width")
    private BigDecimal width;

    @Column(name = "height")
    private BigDecimal height;

    @Column(name = "length_class", nullable = false)
    @Enumerated(EnumType.STRING)
    private LengthClasses lengthClass;

    @NotNull
    @Column(name = "composite", nullable = false)
    private Boolean composite;

    @NotNull
    @Column(name = "sort_key", nullable = false)
    private Integer sortKey;

    @NotNull
    @Builder.Default
    @Column(name = "deactivated", nullable = false)
    private Boolean deactivated = false;

    @NotNull
    @Column(name = "rec_status", nullable = false)
    private Character recStatus;

    @NotNull
    @Builder.Default
    @Column(name = "date_added", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateAdded = Instant.now();

    @Column(name = "date_modified", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateModified;
}