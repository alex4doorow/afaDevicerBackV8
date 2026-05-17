package com.afa.devicer.back.integrations.yandex.map;

import com.afa.core.dto.integrations.yandex.map.YandexGeocodeBriefDto;
import com.afa.core.dto.integrations.yandex.map.YandexGeocodeResponseDto;
import com.afa.core.enums.DevicerErrors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class YandexGeocodeApiConnector {

    @Value("${integrations.yandex.geocode.maps.url}")
    private String url;

    @Value("${integrations.yandex.geocode.maps.key}")
    private String apiKey;

    private final WebClient webClient;

    public YandexGeocodeResponseDto getGeocodeByCity(final String city) {
        try {
            return webClient.get()
                    .uri(url + "?format={format}&apikey={apikey}&geocode={city}",
                            "json",
                            apiKey,
                            city)
                    .retrieve()
                    .bodyToMono(YandexGeocodeResponseDto.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
        } catch (WebClientResponseException | WebClientRequestException e) {
            log.error(DevicerErrors.INTEGRATION_YANDEX_GEOCODE_MAPS_CONNECTION_ERRORS.getErrorMessage(), e);
            return YandexGeocodeResponseDto.createEmpty();
        }
    }

    @SuppressWarnings({"PMD.AvoidLiteralsInIfCondition"})
    public YandexGeocodeBriefDto getPositionByCity(final String city) {
        final YandexGeocodeResponseDto response = getGeocodeByCity(city);

        if (response.getResponse().getGeoObjectCollection().getFeatureMembers() == null) {
            return YandexGeocodeBriefDto.createEmpty();
        }

        for (final YandexGeocodeResponseDto.FeatureMemberDto featureMember : response.getResponse().getGeoObjectCollection().getFeatureMembers()) {

            if (featureMember.getGeoObject() == null
                    || featureMember.getGeoObject().getPoint() == null
                    || StringUtils.isBlank(featureMember.getGeoObject().getPoint().getPosition())) {
                continue;
            }

            final YandexGeocodeResponseDto.AddressDto address = featureMember.getGeoObject()
                    .getMetaDataProperty()
                    .getGeocoderMetaData()
                    .getAddress();

            final String pos = featureMember.getGeoObject().getPoint().getPosition();
            final String countryCode = address == null ? null : address.getCountryCode();
            final String addressLine = address == null ? null : address.getFormatted();

            if ("RU".equals(countryCode)) {
                final String[] posArray = pos.split(" ");
                final Map<String, String> position = Map.of("longitude", posArray[0], "latitude", posArray[1]);
                return YandexGeocodeBriefDto.builder()
                        .countryCode(countryCode)
                        .addressLine(addressLine)
                        .position(position)
                        .build();
            }
        }
        return YandexGeocodeBriefDto.createEmpty();
    }
}
