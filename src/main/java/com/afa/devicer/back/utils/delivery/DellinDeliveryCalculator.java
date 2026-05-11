package com.afa.devicer.back.utils.delivery;

import com.afa.core.dto.delivery.DeliveryCalcParcelDto;
import com.afa.core.dto.integrations.geo.GeoNamesTimezoneResponseDto;
import com.afa.core.enums.AmountTypes;
import com.afa.devicer.back.entities.orders.Order;

import java.math.BigDecimal;
import java.util.Map;

@SuppressWarnings({"PMD.LawOfDemeter"})
public class DellinDeliveryCalculator extends BaseDeliveryCalculator<Void> implements DeliveryCalculator {

    public DellinDeliveryCalculator(
            final Order order,
            final Map<AmountTypes, BigDecimal> amounts,
            final GeoNamesTimezoneResponseDto toAddressTimezone) {
        super(order, amounts, toAddressTimezone,null);
    }

    @Override
    public DeliveryCalcParcelDto calc() {
        final DeliveryCalcParcelDto result = new DeliveryCalcParcelDto();
        result.setDeliveryPrice(BigDecimal.ZERO);
        result.setDeliveryFullPrice(BigDecimal.ZERO);
        result.setDeliverySellerSummary(BigDecimal.ZERO);
        result.setDeliveryCustomerSummary(BigDecimal.ZERO);
        result.setTo(order.getDelivery().getDeliveryType().getAnnotation());
        result.setTermText("уточнить на www.dellin.ru");
        result.setWeightText(calcTotalWeightKg().toPlainString() + " кг.");
        return result;
    }
}
