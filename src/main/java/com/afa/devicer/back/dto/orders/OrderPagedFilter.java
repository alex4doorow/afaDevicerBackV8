package com.afa.devicer.back.dto.orders;

import com.afa.devicer.back.dto.BasePagedFilter;
import com.afa.devicer.back.dto.customers.CustomerConditionsDto;
import com.afa.devicer.back.dto.products.ProductConditionsDto;
import com.afa.devicer.back.enums.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings({"PMD.CommentRequired", "PMD.TooManyFields"})
public class OrderPagedFilter extends BasePagedFilter {

    @NotNull
    @Schema(description = "period exist")
    private boolean periodExist;

    @Schema(description = "period")
    private Pair<LocalDate, LocalDate> period;

    @Schema(description = "order id")
    private UUID id;

    @Schema(description = "order num")
    private Long orderNum;

    @Schema(description = "track code")
    private String trackCode;

    @Schema(description = "delivery address")
    private String deliveryAddress;

    @Schema(description = "track code not exist")
    private boolean trackCodeNotExist;

    @NotNull
    @Valid
    @Schema(description = "product conditions")
    private ProductConditionsDto productConditions;

    @NotNull
    @Valid
    @Schema(description = "customer conditions")
    private CustomerConditionsDto customerConditions;

    @Schema(description = "list of order types")
    private Set<OrderTypes> types;

    @Schema(description = "list of advert types")
    private Set<OrderAdvertTypes> advertTypes;

    @Schema(description = "list of payment types")
    private Set<OrderPaymentTypes> paymentTypes;

    @Schema(description = "list of delivery types")
    private Set<DeliveryTypes> deliveryTypes;

    @Schema(description = "list of statuses")
    private Set<OrderStatusTypes> statuses;
}
