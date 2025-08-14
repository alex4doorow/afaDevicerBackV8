package com.afa.devicer.back.dto.orders;

import com.afa.devicer.back.enums.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order")
public class OrderSaveRequest {

    @NotNull
    @Positive
    @Schema(description = "order num")
    private Long orderNum;

    @NotNull
    @Schema(description = "order_date")
    private LocalDate orderDate;

    @NotNull
    @Schema(description = "customer id")
    private Long customerId;

    @NotNull
    @Schema(description = "order type", example = "ORDER")
    private OrderTypes type;

    @NotNull
    @Schema(description = "source type", example = "CALL")
    private OrderSourceTypes sourceType;

    @NotNull
    @Schema(description = "advert type", example = "ADVERT")
    private OrderAdvertTypes advertType;

    @NotNull
    @Schema(description = "payment type", example = "PREPAYMENT")
    private OrderPaymentTypes paymentType;

    @NotNull
    @Schema(description = "store")
    private StoreTypes store;

    @NotNull
    @PositiveOrZero
    @Schema(description = "product category id")
    private Long productCategoryId;

    @Valid
    @NotNull
    @Schema(description = "delivery")
    private OrderDeliverySaveRequest delivery;

    @Valid
    @NotNull
    @Schema(description = "items")
    @Size(min = 0, max = 20)
    private Set<OrderItemSaveRequest> items;

    @NotBlank
    @Schema(description = "annotation")
    private String annotation;
}
