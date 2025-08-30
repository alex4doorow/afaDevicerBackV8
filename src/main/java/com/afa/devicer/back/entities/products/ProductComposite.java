package com.afa.devicer.back.entities.products;

import com.afa.core.enums.ProductTypes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bp_product_composites",
        indexes = {
                @Index(name = "idx_bp_product_composites_master_product_id", columnList = "master_product_id"),
                @Index(name = "idx_bp_product_composites_slave_product_id", columnList = "slave_product_id"),

        })
@SuppressWarnings({"PMD.TooManyFields", "PMD.AvoidDuplicateLiterals", "PMD.LawOfDemeter"})
public class ProductComposite {

    @Id
    @NotNull
    @SequenceGenerator(name = "d_sequence", sequenceName = "d_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_product_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Product master;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slave_product_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Product slave;

    @NotNull
    @Builder.Default
    @Column(name = "slave_quantity", nullable = false)
    private Integer slaveQuantity = 0;

    @NotNull
    @Builder.Default
    @Column(name = "slave_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductTypes slaveType = ProductTypes.ADDITIONAL;
}
