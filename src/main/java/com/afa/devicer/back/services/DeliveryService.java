package com.afa.devicer.back.services;

import com.afa.core.dto.delivery.DeliveryCalcParcelDto;
import com.afa.core.dto.integrations.geo.GeoNamesTimezoneResponseDto;
import com.afa.core.enums.AmountTypes;
import com.afa.devicer.back.entities.orders.Order;
import com.afa.devicer.back.integrations.BaseConnector;
import com.afa.devicer.back.integrations.cdek.CdekApiConnector;
import com.afa.devicer.back.integrations.geo.GeoTimeZoneApiConnector;
import com.afa.devicer.back.integrations.post.PostCalcApiConnector;
import com.afa.devicer.back.integrations.yandex.map.YandexGeocodeApiConnector;
import com.afa.devicer.back.utils.delivery.DeliveryCalculator;
import com.afa.devicer.back.utils.delivery.DeliveryCalculatorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"PMD.LawOfDemeter"})
public class DeliveryService {

    private final CdekApiConnector cdekApiConnector;
    private final PostCalcApiConnector postCalcApiConnector;
    private final YandexGeocodeApiConnector yandexGeocodeApiConnector;
    private final GeoTimeZoneApiConnector geoTimeZoneApiConnector;

    public DeliveryCalcParcelDto calc(
            final Order order,
            final Map<AmountTypes, BigDecimal> amounts) {

        final DeliveryCalculator deliveryCalculator = DeliveryCalculatorFactory.createCalculator(order, amounts,
                getGeocodeByCity(order.getDelivery().getAddress().getCity()),
                getDeliveryCalculatorAdapter(order));
        return deliveryCalculator.calc();
    }

    private BaseConnector getDeliveryCalculatorAdapter(final Order order) {
        if (order.getDelivery().getDeliveryType().isCdek()) {
            return cdekApiConnector;
        } if (order.getDelivery().getDeliveryType().isPost()) {
            return postCalcApiConnector;
        } else {
            return null;
        }
    }

    private GeoNamesTimezoneResponseDto getGeocodeByCity(final String city) {
        if (StringUtils.isBlank(city)) {
            return null;
        }
        final Map<String, String> position = yandexGeocodeApiConnector.getPositionByCity(city).getPosition();
        return geoTimeZoneApiConnector.getLocalTimeByCity(position);
    }

}
