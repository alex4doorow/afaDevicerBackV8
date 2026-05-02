package com.afa.devicer.back.controllers;

import com.afa.core.dto.dictionaries.CountryFilter;
import com.afa.core.dto.dictionaries.CountryResponse;
import com.afa.devicer.back.services.DictionaryCountryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.afa.devicer.back.controllers.internal.ControllerConstants.DICTIONARIES;
import static com.afa.devicer.back.controllers.internal.ControllerConstants.ROLE_ADMIN;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(DICTIONARIES)
@Tag(name = "Dictionary controller", description = "Operations pertaining to...")
@Secured({ROLE_ADMIN})
public class DictionaryController {

    private final DictionaryCountryService dictionaryCountryService;

    @GetMapping("/countries")
    @Operation(summary = "Find all countries")
    public ResponseEntity<CountryResponse> getProductCategoryFiltered(
            @NotNull @Valid final CountryFilter filter
    ) {
        return ResponseEntity.ok(
                new CountryResponse(dictionaryCountryService.getFiltered(filter))
        );
    }

}
