package com.afa.devicer.back.controllers;

import com.afa.devicer.back.dto.orders.OrderPagedFilter;
import com.afa.devicer.back.dto.orders.OrderPagedResponse;
import com.afa.devicer.back.services.OrderService;
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

    private final UserInfoService userInfoService;
    private final OrderService service;

    @GetMapping()
    @Secured({ROLE_ADMIN})
    @Operation(summary = "orders filtered & paged")
    @Transactional
    public ResponseEntity<OrderPagedResponse> getFiltered(
            @AuthenticationPrincipal final Jwt principal,
            @NotNull @Valid final OrderPagedFilter filter
    ) {
        return ResponseEntity.ok(service.getFiltered(userInfoService.fillUserInfo(principal), filter));
    }

    /*

    @GetMapping()
    @Secured({ROLE_ADMIN, ROLE_INTERNAL, ROLE_EXTERNAL})
    @Operation(summary = "Вакансии filtered & paged")
    @Transactional
    public ResponseEntity<VacancyPagedResponse> getFiltered(
            @AuthenticationPrincipal final Jwt principal,
            @NotNull @Valid final VacancyPagedFilter filter
    ) {
        return ResponseEntity.ok(service.getFiltered(userInfoService.fillUserInfo(principal), filter));
    }

    @GetMapping("/{vacancyId}")
    @Secured({ROLE_ADMIN, ROLE_INTERNAL, ROLE_EXTERNAL})
    @Operation(summary = "Вакансия по идентификатору")
    @Transactional
    public ResponseEntity<VacancySingleResponse> getVacancy(
            @AuthenticationPrincipal final Jwt principal,
            @NotNull @Valid @PathVariable final UUID vacancyId
    ) {
        return ResponseEntity.ok(service.getVacancy(userInfoService.fillUserInfo(principal),
                vacancyId));
    }

    @GetMapping("/{vacancyId}/statusHistory")
    @Secured({ROLE_ADMIN, ROLE_INTERNAL, ROLE_EXTERNAL})
    @Operation(summary = "История статусов запроса")
    public ResponseEntity<VacancyStatusHistoryPagedResponse> statusHistory(
            @AuthenticationPrincipal final Jwt principal,
            @NotNull @Valid @PathVariable final UUID vacancyId,
            @NotNull @Valid final VacancyStatusHistoryPagedFilter filter) {

        return ResponseEntity.ok(service.getStatusHistory(userInfoService.fillUserInfo(principal),
                vacancyId, filter));
    }


    @PostMapping()
    @Secured({ROLE_INTERNAL})
    @Operation(summary = "Сохранить новую вакансию")
    public ResponseEntity<VacancyDto> create(
            @AuthenticationPrincipal final Jwt principal,
            @NotNull @Valid @RequestBody final VacancySaveRequest request
    ) {
        return ResponseEntity.ok(
                mapper.fromVacancy(service.create(userInfoService.fillUserInfo(principal), request))
        );
    }

    @PutMapping("/{vacancyId}")
    @Secured({ROLE_INTERNAL})
    @Operation(summary = "Редактирование вакансии")
    public ResponseEntity<VacancyDto> edit(
            @AuthenticationPrincipal final Jwt principal,
            @NotNull @Valid @PathVariable final UUID vacancyId,
            @NotNull @Valid @RequestBody final VacancySaveRequest request
    ) {
        return ResponseEntity.ok(
                mapper.fromVacancy(service.edit(userInfoService.fillUserInfo(principal), vacancyId, request))
        );
    }

    @PatchMapping("/{vacancyId}/editRate")
    @Secured({ROLE_INTERNAL})
    @Operation(summary = "Редактирование ставки")
    public ResponseEntity<VacancyDto> editRate(
            @AuthenticationPrincipal final Jwt principal,
            @NotNull @Valid @PathVariable final UUID vacancyId,
            @NotNull @Valid @RequestBody final VacancyEditRateRequest request
    ) {
        return ResponseEntity.ok(
                mapper.fromVacancy(service.editRate(userInfoService.fillUserInfo(principal), vacancyId, request))
        );
    }

    */
}
