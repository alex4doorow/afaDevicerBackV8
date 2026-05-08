package com.afa.devicer.back.controllers;

import com.afa.core.dto.BaseResponse;
import com.afa.core.dto.orders.*;
import com.afa.devicer.back.mappers.OrderMapper;
import com.afa.devicer.back.services.OrderService;
import com.afa.devicer.back.services.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import static com.afa.devicer.back.controllers.internal.ControllerConstants.ORDERS;
import static com.afa.devicer.back.controllers.internal.ControllerConstants.ROLE_ADMIN;

@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(ORDERS)
@Tag(name = "Orders", description = "Orders controller")
@Secured({ROLE_ADMIN})
public class OrderController {

    private final UserInfoService userInfoService;
    private final OrderService service;
    private final OrderMapper mapper;

    @GetMapping("/next-order-num")
    public ResponseEntity<Long> findNexOrderNum() {
        return ResponseEntity.ok(service.findNexOrderNum());
    }

    @PostMapping("/full-filtered")
    @Operation(summary = "orders filtered & paged")
    public ResponseEntity<OrderPagedResponse> getFiltered(
            @AuthenticationPrincipal final Jwt principal,
            @NotNull @Valid @RequestBody final OrderPagedFilter filter
    ) {
        return ResponseEntity.ok(service.getFiltered(userInfoService.fillUserInfo(principal), filter));
    }

    @GetMapping("/simple-filtered")
    @Operation(summary = "orders filtered by single text")
    public ResponseEntity<OrderPagedResponse> getSimpleFiltered(
            @AuthenticationPrincipal final Jwt principal,
            @NotNull @RequestParam("dirtyConditions") final String dirtyConditions) {

        return ResponseEntity.ok(service.getSimpleFiltered(userInfoService.fillUserInfo(principal), dirtyConditions));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Order по идентификатору")
    public ResponseEntity<OrderSingleResponse> getOrder(
            @NotNull @Valid @PathVariable final Long orderId
    ) {
        return ResponseEntity.ok(service.getOrder(orderId));
    }

    @PostMapping()
    @Operation(summary = "Сохранить заявку нового заказа")
    public ResponseEntity<OrderSingleResponse> create(
            @AuthenticationPrincipal final Jwt principal,
            @NotNull @Valid @RequestBody final OrderSaveRequest request
    ) {
        return ResponseEntity.ok(
                new OrderSingleResponse(mapper.fromOrder(service.create(userInfoService.fillUserInfo(principal), request)))
        );
    }

    @PutMapping("/{orderId}")
    @Operation(summary = "Редактирование order")
    public ResponseEntity<OrderSingleResponse> edit(
            @AuthenticationPrincipal final Jwt principal,
            @NotNull @Valid @PathVariable final Long orderId,
            @NotNull @Valid @RequestBody final OrderSaveRequest request
    ) {
        return ResponseEntity.ok(
                new OrderSingleResponse(mapper.fromOrder(service.edit(userInfoService.fillUserInfo(principal), orderId, request)))
        );
    }

    @PatchMapping("/{orderId}/change-status")
    @Operation(summary = "Change status order")
    public ResponseEntity<OrderSingleResponse> changeFullStatus(
            @AuthenticationPrincipal final Jwt principal,
            @NotNull @Valid @PathVariable final Long orderId,
            @NotNull @Valid @RequestBody final OrderChangeStatusSaveRequest request
    ) {
        return ResponseEntity.ok(
                new OrderSingleResponse(mapper.fromOrder(service.changeFullStatus(userInfoService.fillUserInfo(principal), orderId, request)))
        );
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "Delete order")
    public ResponseEntity<BaseResponse> delete(
            @AuthenticationPrincipal final Jwt principal,
            @NotNull @Valid @PathVariable final Long orderId
    ) {
        service.delete(userInfoService.fillUserInfo(principal), orderId);
        return ResponseEntity.ok(new BaseResponse());
    }

}
