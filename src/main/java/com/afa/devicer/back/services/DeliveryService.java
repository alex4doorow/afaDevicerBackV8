package com.afa.devicer.back.services;

import com.afa.core.dto.delivery.DeliveryCalcParcelDto;
import com.afa.core.enums.AmountTypes;
import com.afa.devicer.back.entities.orders.Order;
import com.afa.devicer.back.integrations.BaseConnector;
import com.afa.devicer.back.integrations.cdek.CdekApiConnector;
import com.afa.devicer.back.utils.delivery.DeliveryCalculator;
import com.afa.devicer.back.utils.delivery.DeliveryCalculatorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"PMD.LawOfDemeter"})
public class DeliveryService {

    private final CdekApiConnector cdekApiConnector;

    public DeliveryCalcParcelDto calc(
            final Order order,
            final Map<AmountTypes, BigDecimal> amounts) {

        final DeliveryCalculator deliveryCalculator = DeliveryCalculatorFactory.createCalculator(order, amounts,
                getDeliveryCalculatorAdapter(order));
        return deliveryCalculator.calc();
    }

    private BaseConnector getDeliveryCalculatorAdapter(final Order order) {
        if (order.getDelivery().getDeliveryType().isCdek()) {
            return cdekApiConnector;
        } else {
            return null;
        }
    }
}
