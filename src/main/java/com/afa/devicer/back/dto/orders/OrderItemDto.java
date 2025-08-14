package com.afa.devicer.back.dto.orders;

import com.afa.devicer.back.dto.products.ProductShortDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order delivery info")
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("PMD.TooManyFields")
public class OrderItemDto {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "item num")
    private Long itemNum;

    @Schema(description = "product")
    private ProductShortDto product;

    @Schema(description = "price")
    private BigDecimal price;

    @Schema(description = "quantity")
    private Integer quantity;

    @Schema(description = "discount rate")
    private Integer discountRate;

    @Schema(description = "amount")
    private BigDecimal amount;

    @Schema(description = "amount supplier")
    private BigDecimal amountSupplier;
}
