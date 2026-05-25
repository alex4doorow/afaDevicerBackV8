package com.afa.devicer.back.controllers;

import com.afa.core.dto.BaseResponse;
import com.afa.core.dto.people.PersonSettingsResponse;
import com.afa.core.dto.people.PersonSettingsSaveRequest;
import com.afa.devicer.back.services.PersonService;
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

import static com.afa.devicer.back.controllers.internal.ControllerConstants.PERSONS;
import static com.afa.devicer.back.controllers.internal.ControllerConstants.ROLE_ADMIN;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(PERSONS)
@Secured({ROLE_ADMIN})
public class PersonController {

    private final UserInfoService userInfoService;
    private final PersonService service;

    @GetMapping("/settings")
    @Transactional(readOnly = true)
    public ResponseEntity<PersonSettingsResponse> getSettings(
            @AuthenticationPrincipal final Jwt principal
    ) {
        return ResponseEntity.ok(
                new PersonSettingsResponse(service.getSettings(userInfoService.fillUserInfo(principal).getKeycloakUuid()))
        );
    }

    @PostMapping("settings")
    public ResponseEntity<BaseResponse> saveSettings(
            @AuthenticationPrincipal final Jwt principal,
            @NotNull @Valid @RequestBody final PersonSettingsSaveRequest request
    ) {
        service.saveSettings(userInfoService.fillUserInfo(principal).getKeycloakUuid(), request);
        return ResponseEntity.ok(new BaseResponse());
    }


}
