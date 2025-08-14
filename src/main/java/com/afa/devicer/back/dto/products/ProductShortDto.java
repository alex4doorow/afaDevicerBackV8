package com.afa.devicer.back.dto.products;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "product info")
public class ProductShortDto {

    @Schema(description = "id")
    private Long id;

    @Column(name = "sku")
    private String sku;

    @Column(name = "short name")
    private String shortName;

    @Column(name = "long name")
    private String longName;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "composite")
    private Boolean composite;

    @Column(name = "deactivated")
    private Boolean deactivated;

}
