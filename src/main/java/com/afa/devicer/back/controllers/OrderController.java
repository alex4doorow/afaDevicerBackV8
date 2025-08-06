package com.afa.devicer.back.controllers;

import com.afa.devicer.back.dto.orders.OrderPagedFilter;
import com.afa.devicer.back.dto.orders.OrderPagedResponse;
import com.afa.devicer.back.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.afa.devicer.back.controllers.internal.ControllerConstants.ORDERS;
import static com.afa.devicer.back.controllers.internal.ControllerConstants.ROLE_ADMIN;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(ORDERS)
@Tag(name = "Orders", description = "Orders controller")
@SuppressWarnings({"PMD.ExcessiveImports"})
@Slf4j
public class OrderController {

    private final OrderService service;

    @GetMapping()
    @Secured({ROLE_ADMIN})
    @Operation(summary = "orders filtered & paged")
    @Transactional
    public ResponseEntity<OrderPagedResponse> getFiltered(
            @AuthenticationPrincipal final Jwt principal,
            @NotNull @Valid final OrderPagedFilter filter
    ) {
        log.info("token: {}", principal.getTokenValue());
        return ResponseEntity.ok(service.getFiltered(filter));
    }
}
