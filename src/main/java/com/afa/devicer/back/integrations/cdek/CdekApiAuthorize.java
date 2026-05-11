package com.afa.devicer.back.integrations.cdek;

import com.afa.core.dto.integrations.cdek.CdekAccessDto;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import com.afa.devicer.back.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@RequiredArgsConstructor
public class CdekApiAuthorize {

    @Value("${integrations.cdek.protocol}")
    private String protocol;
    @Value("${integrations.cdek.host}")
    private String host;
    @Value("${integrations.cdek.account}")
    private String account;
    @Value("${integrations.cdek.secure}")
    private String secure;

    private final WebClient webClient;

    @Cacheable(CacheConfig.CACHE_CDEK_ACCESS)
    public CdekAccessDto authorization() {

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
