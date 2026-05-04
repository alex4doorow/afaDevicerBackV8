package com.afa.devicer.back.integrations.cdek;

import com.afa.core.dto.integrations.cdek.CdekAccessDto;
import com.afa.core.dto.integrations.cdek.CdekCityDto;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CdekApiConnector {

    @Value("${integrations.cdek.protocol}")
    private String protocol;
    @Value("${integrations.cdek.host}")
    private String host;
    @Value("${integrations.cdek.account}")
    private String account;
    @Value("${integrations.cdek.secure}")
    private String secure;

    private final WebClient webClient;

    @Transactional(readOnly = true)
    public List<CdekCityDto> getCities(
            final String countryCode2,
            final String cityContext) {
        try {
            final CdekAccessDto access = authorization();
            final CdekCityDto[] cdekCityDtoArray = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme(protocol)
                            .host(host)
                            .path("/v2/location/cities")
                            .queryParam("country_codes", countryCode2)
                            .queryParam("city", cityContext)
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

    private CdekAccessDto authorization() {

        final MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", account);
        formData.add("client_secret", secure);
        formData.add("grant_type", "client_credentials");
        try {
            final String uri = protocol + "://" + host + "/v2/oauth/token";
            return webClient.post()
                    .uri(new URI(uri))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromMultipartData(formData))
                    .retrieve()
                    .bodyToMono(CdekAccessDto.class)
                    .retry(3)
                    .log()
                    .block();
        } catch (URISyntaxException | WebClientResponseException | WebClientRequestException e) {
            throw new DevicerException(DevicerErrors.INTEGRATION_CDEK_AUTHORIZATION_ERRORS, e);
        }
    }
}
