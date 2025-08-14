package com.afa.devicer.back.dto.orders;

import com.afa.devicer.back.dto.dictionaries.AddressSaveRequest;
import com.afa.devicer.back.dto.persons.PersonSaveRequest;
import com.afa.devicer.back.enums.DeliveryPriceTypes;
import com.afa.devicer.back.enums.DeliveryTypes;
import com.afa.devicer.back.enums.DeliveryPaymentTypes;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order delivery save request")
public class OrderDeliverySaveRequest {

    @NotNull
    @Schema(description = "delivery type")
    private DeliveryTypes deliveryType;

    @NotNull
    @Schema(description = "payment delivery type")
    private DeliveryPaymentTypes deliveryPaymentType;

    @Schema(description = "delivery price type")
    private DeliveryPriceTypes deliveryPriceType;

    @NotNull
    @PositiveOrZero
    @Schema(description = "price")
    private BigDecimal price;

    @Valid
    @NotNull
    @Schema(description = "address")
    private AddressSaveRequest address;

    @Valid
    @NotNull
    @Schema(description = "recipient")
    private PersonSaveRequest recipient;

    @Schema(description = "annotation")
    private String annotation;

    @Future
    @Schema(description = "delivery date")
    private LocalDate deliveryDate;

    @Schema(description = "time in")
    private LocalTime timeIn;

    @Schema(description = "time out")
    private LocalTime timeOut;
}
