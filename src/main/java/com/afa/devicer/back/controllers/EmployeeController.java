package com.afa.devicer.back.controllers;

import com.afa.core.dto.BaseResponse;
import com.afa.core.dto.employee.EmployeeSettingsResponse;
import com.afa.core.dto.employee.EmployeeSettingsSaveRequest;
import com.afa.devicer.back.services.EmployeeService;
import com.afa.devicer.back.services.UserInfoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.afa.devicer.back.controllers.internal.ControllerConstants.EMPLOYEES;
import static com.afa.devicer.back.controllers.internal.ControllerConstants.ROLE_ADMIN;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(EMPLOYEES)
public class EmployeeController {

    private final UserInfoService userInfoService;
    private final EmployeeService service;

    @GetMapping("/settings")
    @Secured({ROLE_ADMIN})
    @Transactional(readOnly = true)
    public ResponseEntity<EmployeeSettingsResponse> getSettings(
            @AuthenticationPrincipal final Jwt principal
    ) {
        return ResponseEntity.ok(
                new EmployeeSettingsResponse(service.getSettings(userInfoService.fillUserInfo(principal).getKeycloakUuid()))
        );
    }

    @PostMapping("settings")
    @Secured({ROLE_ADMIN})
    public ResponseEntity<BaseResponse> saveSettings(
            @AuthenticationPrincipal final Jwt principal,
            @NotNull @Valid @RequestBody final EmployeeSettingsSaveRequest request
    ) {
        service.saveSettings(userInfoService.fillUserInfo(principal).getKeycloakUuid(), request);
        return ResponseEntity.ok(new BaseResponse());
    }
}
