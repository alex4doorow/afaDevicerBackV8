package com.afa.devicer.back.dto.products;

import com.afa.devicer.back.enums.ProductTypes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings({"PMD.CommentRequired", "PMD.TooManyFields"})
public class ProductConditionsDto {

    @Schema(description = "product id")
    private int id;

    @Schema(description = "short name")
    private String shortName;

    @Schema(description = "sku")
    private String sku;

    @Schema(description = "product category")
    private ProductCategoryDto category;

    @Schema(description = "product type")
    private ProductTypes type;
}
