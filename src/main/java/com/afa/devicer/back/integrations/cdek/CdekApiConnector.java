package com.afa.devicer.back.integrations.cdek;

import com.afa.core.dto.integrations.cdek.*;
import com.afa.core.enums.DeliveryTypes;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import com.afa.devicer.back.integrations.BaseConnector;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class CdekApiConnector implements BaseConnector {

    @Value("${integrations.cdek.protocol}")
    private String protocol;
    @Value("${integrations.cdek.host}")
    private String host;
    @Getter
    @Value("${integrations.cdek.from.location.city.code}")
    private Integer fromLocationCityCode;

    private final WebClient webClient;
    private final CdekApiAuthorize authorize;

    @Transactional(readOnly = true)
    public List<CdekCityDto> getLocationCities(final CdekCityFilter filter) {
        try {
            final CdekAccessDto access = authorize.authorization();
            final CdekCityDto[] cdekCityDtoArray = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme(protocol)
                            .host(host)
                            .path("/v2/location/cities")
                            .queryParam("country_codes", filter.getCountryCode2())
                            .queryParam("city", filter.getCityNameContext())
                            .queryParam("lang", "rus")
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, access.getSecret())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(CdekCityDto[].class)
                    .log()
                    .block();
            if (cdekCityDtoArray == null || cdekCityDtoArray.length == 0) {
                return Collections.emptyList();
            }
            return List.of(cdekCityDtoArray);
        } catch (WebClientResponseException | WebClientRequestException e) {
            throw new DevicerException(DevicerErrors.INTEGRATION_CDEK_CITIES_ERRORS, e);
        }
    }

    public List<CdekDeliveryPointDto> getDeliveryPoints(final CdekDeliveryPointFilter filter) {
        try {
            final CdekAccessDto access = authorize.authorization();
            final CdekDeliveryPointDto[] cdekDeliveryPointDtoArray = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme(protocol)
                            .host(host)
                            .path("/v2/deliverypoints")
                            .queryParam("country_codes", "RU")
                            .queryParam("city_code", filter.getCityCode())
                            .queryParam("type", "PVZ")
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, access.getSecret())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(CdekDeliveryPointDto[].class)
                    .log()
                    .block();
            if (cdekDeliveryPointDtoArray == null || cdekDeliveryPointDtoArray.length == 0) {
                return Collections.emptyList();
            }
            return Stream.of(cdekDeliveryPointDtoArray)
                    .sorted(Comparator.comparing(CdekDeliveryPointDto::getId))
                    .toList();
        } catch (WebClientResponseException | WebClientRequestException e) {
            throw new DevicerException(DevicerErrors.INTEGRATION_CDEK_DELIVERY_POINTS_ERRORS, e);
        }
    }

    public CdekCalculatorTariffResponse calculateTariff(final CdekCalculatorTariffRequest request) {
        final String uri = protocol + "://" + host + "/v2/calculator/tariff";
        try {
            final CdekAccessDto access = authorize.authorization();
            return webClient.post()
                    .uri(new URI(uri))
                    .header(HttpHeaders.AUTHORIZATION, access.getSecret())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(CdekCalculatorTariffResponse.class)
                    .log()
                    .timeout(Duration.ofSeconds(5))
                    .block();
        } catch (URISyntaxException | WebClientResponseException | WebClientRequestException e) {
            throw new DevicerException(DevicerErrors.INTEGRATION_CDEK_CALC_TARIFF_ERRORS, e);
        }
    }

    public String widgetProxy(String body) {
//        серверная часть этой запчасти
//        var widjet = new ISDEKWidjet({
//                path: 'https://pribormaster.ru/catalog/view/theme/zemez808/js/cdek-pvzwidget/scripts/',
//                servicepath: 'https://pribormaster.ru/catalog/controller/extension/shipping/cdek/service.php'
        // тут:
        // 1. получить OAuth token СДЭК
        // 2. понять method/action из body
        // 3. сходить в CDEK API
        // 4. вернуть JSON обратно виджету

        //throw new UnsupportedOperationException("TODO implement CDEK widget proxy");
        return "{}";
    }

    public Integer getCdekTariffId(final DeliveryTypes deliveryType) {
        final int tariffId;
        if (deliveryType == DeliveryTypes.CDEK_COURIER) {
            tariffId = 137;
        } else if (deliveryType == DeliveryTypes.CDEK_COURIER_ECONOMY) {
            tariffId = 233;
        } else if (deliveryType == DeliveryTypes.CDEK_PVZ_TYPICAL) {
            tariffId = 136;
        } else if (deliveryType == DeliveryTypes.PICKUP) {
            tariffId = 136;
        } else if (deliveryType == DeliveryTypes.CDEK_PVZ_ECONOMY) {
            tariffId = 234;
        } else {
            tariffId = 0;
        }
        return tariffId;
    }
}
