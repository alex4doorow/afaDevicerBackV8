package com.afa.devicer.back.entities.products;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "d_product_categories")
public class ProductCategory {

    @Id
    @NotNull
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @Column(name = "group_type", nullable = false, length = 128)
    private String groupType;

    @NotNull
    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @NotNull
    @Column(name = "rec_status", nullable = false)
    private Character recStatus;

    @NotNull
    @Column(name = "date_added", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateAdded = Instant.now();

    @Column(name = "date_modified", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateModified;
}
