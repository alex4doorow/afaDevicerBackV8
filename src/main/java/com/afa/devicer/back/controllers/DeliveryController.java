package com.afa.devicer.back.controllers;

import com.afa.core.dto.delivery.*;
import com.afa.core.enums.AmountTypes;
import com.afa.devicer.back.entities.orders.Order;
import com.afa.devicer.back.mappers.OrderMapper;
import com.afa.devicer.back.services.DeliveryService;
import com.afa.devicer.back.services.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

import static com.afa.devicer.back.controllers.internal.ControllerConstants.DELIVERY;
import static com.afa.devicer.back.controllers.internal.ControllerConstants.ROLE_ADMIN;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(DELIVERY)
@Tag(name = "Delivery controller", description = "delivery operations of orders")
@Secured({ROLE_ADMIN})
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping("/calc/parcel-delivery-amounts")
    public ResponseEntity<DeliveryCalcParcelAmountsResponse> calcParcelDeliveryAmounts(
            @Valid @RequestBody final DeliveryCalcParcelAmountsRequest request) {

        final Order order = orderMapper.fromRequest(request.getOrder());
        final Map<AmountTypes, BigDecimal> amounts = orderService.calcTotalAmounts(order);
        final DeliveryCalcParcelDto deliveryCalcParcel = deliveryService.calc(order, amounts);

        return ResponseEntity.ok(new DeliveryCalcParcelAmountsResponse(deliveryCalcParcel));
    }

    @PostMapping("/calc/parcel-delivery-prices")
    public ResponseEntity<DeliveryPricesResponse> findPricesByDeliveryType(
            @Valid @RequestBody final DeliveryPricesRequest request) {

        return ResponseEntity.ok(new DeliveryPricesResponse(deliveryService.findPricesByDeliveryType(request)));
    }
}
