package com.afa.devicer.back.controllers;

import com.afa.devicer.back.dto.customers.CustomerDto;
import com.afa.devicer.back.dto.customers.CustomerSaveRequest;
import com.afa.devicer.back.dto.customers.CustomerSingleResponse;
import com.afa.devicer.back.mappers.CustomerMapper;
import com.afa.devicer.back.services.CustomerService;
import com.afa.devicer.back.services.UserInfoService;
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
import org.springframework.web.bind.annotation.*;

import static com.afa.devicer.back.controllers.internal.ControllerConstants.CUSTOMERS;
import static com.afa.devicer.back.controllers.internal.ControllerConstants.ROLE_ADMIN;

@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(CUSTOMERS)
@Tag(name = "Customers", description = "Customers controller")
public class CustomerController {

    private final UserInfoService userInfoService;
    private final CustomerService service;
    private final CustomerMapper mapper;

    @GetMapping("/{customerId}")
    @Secured({ROLE_ADMIN})
    @Operation(summary = "Customer по идентификатору")
    @Transactional
    public ResponseEntity<CustomerSingleResponse> getCustomer(
            @AuthenticationPrincipal final Jwt principal,
            @NotNull @Valid @PathVariable final Long customerId
    ) {
        return ResponseEntity.ok(service.getCustomer(userInfoService.fillUserInfo(principal), customerId));
    }

    @PostMapping()
    @Secured({ROLE_ADMIN})
    @Operation(summary = "Save new customer")
    public ResponseEntity<CustomerDto> create(
            @AuthenticationPrincipal final Jwt principal,
            @NotNull @Valid @RequestBody final CustomerSaveRequest request
    ) {
        return ResponseEntity.ok(mapper.fromCustomer(service.create(userInfoService.fillUserInfo(principal), request)));
    }
}
