package com.afa.devicer.back.dto.products;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "product category info")
public class ProductCategoryDto {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "type group")
    private String groupType;

    @Column(name = "name")
    private String name;
}
