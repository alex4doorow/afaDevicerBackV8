package com.afa.devicer.back.controllers.integrations;

import com.afa.core.dto.integrations.cdek.*;
import com.afa.devicer.back.integrations.cdek.CdekApiConnector;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.afa.devicer.back.controllers.internal.ControllerConstants.INTEGRATIONS_CDEK;

@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(INTEGRATIONS_CDEK)
public class CdekController {

    private final CdekApiConnector cdekApiService;

    // cdek api https://api.edu.cdek.ru/v2/location/cities
    // backend https://api/v8/integrations/cdek/location/cities
    // web https://web/wiki/integrations/cdek/location/cities
    @GetMapping("/location/cities")
    public ResponseEntity<CdekCityResponse> getLocationCities(
            @NotNull @Valid final CdekCityFilter filter
    ) {
        return ResponseEntity.ok(
                new CdekCityResponse(cdekApiService.getLocationCities(filter))
        );
    }

    @GetMapping("/deliveryPoints")
    public ResponseEntity<CdekDeliveryPointResponse> getDeliveryPoints(
            @NotNull @Valid final CdekDeliveryPointFilter filter
    ) {
        return ResponseEntity.ok(
                new CdekDeliveryPointResponse(cdekApiService.getDeliveryPoints(filter))
        );
    }

    @PostMapping("/cdek/widget")
    public ResponseEntity<String> widget(@RequestBody final String body) {
        return ResponseEntity.ok(cdekApiService.widgetProxy(body));
    }

}
