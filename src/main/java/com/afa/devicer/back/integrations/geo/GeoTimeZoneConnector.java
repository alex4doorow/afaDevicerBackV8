package com.afa.devicer.back.integrations.geo;

import com.afa.core.dto.integrations.geo.GeoNamesTimezoneResponseDto;
import com.afa.core.enums.DevicerErrors;
import com.afa.devicer.back.integrations.BaseConnector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeoTimeZoneConnector implements BaseConnector {

    @Value("${integrations.geonames.url}")
    private String url;

    @Value("${integrations.geonames.user}")
    private String userName;

    private final WebClient webClient;

    public GeoNamesTimezoneResponseDto getLocalTimeByCity(final Map<String, String> position) {

        try {
            return webClient.get()
                    .uri(url + "?lat={lat}&lng={lng}&username={username}",
                            position.get("latitude"),
                            position.get("longitude"),
                            userName)
                    .retrieve()
                    .bodyToMono(GeoNamesTimezoneResponseDto.class)
                    .block();

        } catch (RuntimeException e) {
            log.error(DevicerErrors.INTEGRATION_GEONAMES_CONNECTION_ERRORS.getErrorMessage(), e);
            return GeoNamesTimezoneResponseDto.createEmpty();
        }
    }
}
