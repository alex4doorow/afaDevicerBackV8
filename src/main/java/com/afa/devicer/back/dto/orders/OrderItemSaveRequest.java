package com.afa.devicer.back.dto.orders;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order item save request")
public class OrderItemSaveRequest {

    @NotNull
    @Schema(description = "item num")
    private Integer itemNum;

    @NotNull
    @Positive
    @Schema(description = "product id")
    private Long productId;

    @NotNull
    @PositiveOrZero
    @Schema(description = "price")
    private BigDecimal price;

    @NotNull
    @Positive
    @Schema(description = "quantity")
    private Integer quantity;

    @PositiveOrZero
    @NotNull
    @Schema(description = "discount rate")
    private BigDecimal discountRate;

}
