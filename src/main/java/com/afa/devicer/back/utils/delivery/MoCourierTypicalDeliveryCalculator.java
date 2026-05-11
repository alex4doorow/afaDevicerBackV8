package com.afa.devicer.back.utils.delivery;

import com.afa.core.dto.delivery.DeliveryCalcParcelDto;
import com.afa.core.dto.integrations.geo.GeoNamesTimezoneResponseDto;
import com.afa.core.enums.AmountTypes;
import com.afa.devicer.back.entities.orders.Order;

import java.math.BigDecimal;
import java.util.Map;

@SuppressWarnings({"PMD.LawOfDemeter"})
public class MoCourierTypicalDeliveryCalculator extends BaseDeliveryCalculator<Void> implements DeliveryCalculator {

    public MoCourierTypicalDeliveryCalculator(
            final Order order,
            final Map<AmountTypes, BigDecimal> amounts,
            final GeoNamesTimezoneResponseDto toAddressTimezone) {
        super(order, amounts, toAddressTimezone, null);
    }

    @Override
    public DeliveryCalcParcelDto calc() {
        final BigDecimal deliveryPrice = order.getDelivery().getDeliveryPriceType().getPrice();
        final DeliveryCalcParcelDto result = new DeliveryCalcParcelDto();
        result.setDeliveryPrice(deliveryPrice);
        result.setDeliveryFullPrice(deliveryPrice);
        result.setDeliverySellerSummary(BigDecimal.ZERO);
        result.setDeliveryCustomerSummary(deliveryPrice);
        result.setTo("курьер, Подмосковье");
        result.setTermText(order.getDelivery().getDeliveryPriceType().getAnnotation());
        result.setWeightText(calcTotalWeightKg().toPlainString() + " кг.");
        return result;
    }
}
