package com.afa.devicer.back.utils.delivery;

import com.afa.core.dto.integrations.geo.GeoNamesTimezoneResponseDto;
import com.afa.core.enums.AmountTypes;
import com.afa.core.enums.DeliveryPaymentMethods;
import com.afa.core.utils.NumericHelper;
import com.afa.devicer.back.entities.orders.Order;

import java.math.BigDecimal;
import java.util.Map;

public abstract class BaseDeliveryCalculator<CONNECTOR> {

    protected final Order order;
    protected final Map<AmountTypes, BigDecimal> amounts;
    protected final GeoNamesTimezoneResponseDto toLocationTimezone;
    /**
     * Интеграция, коннектор к внешнему API - получение данных, выполнение расчетов
     */
    protected final CONNECTOR connector;

    public BaseDeliveryCalculator(
            final Order order,
            final Map<AmountTypes, BigDecimal> amounts,
            final GeoNamesTimezoneResponseDto toLocationTimezone,
            final CONNECTOR connector) {
        this.order = order;
        this.amounts = amounts;
        this.toLocationTimezone = toLocationTimezone;
        this.connector = connector;
    }

    protected BigDecimal calcTotalWeightKg() {
        return NumericHelper.weightG2Kg(amounts.get(AmountTypes.TOTAL_WEIGHT_GRAM).intValue());
    }

    protected DeliveryPaymentMethods getDeliveryPaymentMethod() {
        return order.getItems().stream()
                .findFirst()
                .map(item -> item.getProduct().getDeliveryPaymentMethod())
                .orElse(DeliveryPaymentMethods.FULL);
    }
}
