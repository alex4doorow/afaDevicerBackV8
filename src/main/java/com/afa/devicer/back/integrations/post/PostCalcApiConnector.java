package com.afa.devicer.back.integrations.post;

import com.afa.core.dto.integrations.post.PostCalcTariffRequest;
import com.afa.core.dto.integrations.post.PostCalcTariffResponse;
import com.afa.core.enums.DeliveryTypes;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.enums.OrderPaymentTypes;
import com.afa.core.exceptions.DevicerException;
import com.afa.devicer.back.integrations.BaseConnector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCalcApiConnector implements BaseConnector {

    @Value("${integrations.post.postCalc.protocol}")
    private String protocol;

    @Value("${integrations.post.postCalc.host}")
    private String host;

    @Value("${integrations.post.postCalc.key}")
    private String key;

    @Value("${integrations.post.postCalc.from.index}")
    private String fromIndex;

    private final WebClient webClient;

    public PostCalcTariffResponse calculateTariff(final PostCalcTariffRequest request) {
        try {
            final String uri = protocol + "://" + host + "/?o=JSON&f={from}&t={to}&w={weight}&v={valuation}&key={key}"
                    + getSv(request.getPaymentType());
            return webClient.get()
                    .uri(uri,
                            fromIndex,
                            request.getTo().getPostCode(),
                            request.getWeightOfG(),
                            request.getTotalAmount().intValue(),
                            key)
                    .retrieve()
                    .bodyToMono(PostCalcTariffResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

        } catch (WebClientResponseException | WebClientRequestException e) {
            throw new DevicerException(DevicerErrors.INTEGRATION_POST_CALC_TARIFF_ERRORS, e);
        }
    }

    public String getParcelDataName(final DeliveryTypes type) {
        if (type == DeliveryTypes.POST_TYPICAL) {
            return "ЦеннаяПосылка";
        } else if (type == DeliveryTypes.POST_EMS) {
            return "EMS";
        } else if (type == DeliveryTypes.POST_I_CLASS) {
            return "Посылка1Класс";
        } else {
            return "";
        }
    }

    private String getSv(final OrderPaymentTypes paymentType) {

        if (paymentType == OrderPaymentTypes.PREPAYMENT) {
            return "&ib=p&pk=50&sv=sm,ng";
        } else {
            return "&ib=f&pk=50&sv=sm,cod,ng";
        }
    }
}
