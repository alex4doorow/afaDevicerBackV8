package com.afa.devicer.back.controllers.integrations;

import com.afa.core.dto.BaseResponse;
import com.afa.core.dto.integrations.cdek.CdekCityDto;
import com.afa.core.dto.integrations.cdek.CdekCityFilter;
import com.afa.devicer.back.integrations.cdek.CdekApiConnector;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.afa.devicer.back.controllers.internal.ControllerConstants.INTEGRATIONS;

@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(INTEGRATIONS)
public class CdekController {

    private final CdekApiConnector cdekApiService;

    // cdek api https://api.edu.cdek.ru/v2/location/cities
    // backend https://api/v8/integrations/cdek/location/cities
    // web https://web/wiki/integrations/cdek/location/cities
    @GetMapping("/cdek/location/cities")
    public ResponseEntity<BaseResponse> getLocationCities(
            @NotNull @Valid final CdekCityFilter filter
    ) {

        final List<CdekCityDto> cities = cdekApiService.getCities("RU", "Москва");
        log.info("getCities(): {}", cities);

        return ResponseEntity.ok(
                new BaseResponse()
        );
    }


}
