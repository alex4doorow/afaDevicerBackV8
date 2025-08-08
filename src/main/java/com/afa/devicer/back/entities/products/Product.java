package com.afa.devicer.back.entities.products;

import com.afa.devicer.back.entities.dictionaries.Manufacture;
import com.afa.devicer.back.entities.dictionaries.ProductCategory;
import com.afa.devicer.back.entities.dictionaries.StockStatus;
import com.afa.devicer.back.enums.LengthClasses;
import com.afa.devicer.back.enums.WeightClasses;
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
                @Index(name = "idx_bp_products_sku", columnList = "sku")
        })
@SuppressWarnings({"PMD.TooManyFields", "PMD.AvoidDuplicateLiterals", "PMD.LawOfDemeter"})
public class Product {

    @Id
    @NotNull
    @SequenceGenerator(name = "d_sequence", sequenceName = "d_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "sku", nullable = false)
    private String sku;

    @NotNull
    @Column(name = "short_name", nullable = false)
    private String shortName;

    @Column(name = "long_name")
    private String longName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "minimum")
    private Integer minimum;

    @Column(name = "price")
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_category_id")
    @ToString.Exclude
    @JsonIgnore
    private ProductCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacture_id")
    @ToString.Exclude
    @JsonIgnore
    private Manufacture manufacture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_status_id")
    @ToString.Exclude
    @JsonIgnore
    private StockStatus stockStatus;

    @Column(name = "date_available")
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

    @Column(name = "composite")
    private Boolean composite;

    @Column(name = "sort_key")
    private Integer sortKey;

    @Builder.Default
    @Column(name = "deactivated")
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