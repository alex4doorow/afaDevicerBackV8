package com.afa.devicer.back.utils.delivery;

import com.afa.core.dto.delivery.DeliveryCalcParcelDto;
import com.afa.core.dto.integrations.geo.GeoNamesTimezoneResponseDto;
import com.afa.core.enums.AmountTypes;
import com.afa.devicer.back.entities.orders.Order;

import java.math.BigDecimal;
import java.util.Map;

@SuppressWarnings({"PMD.LawOfDemeter"})
public class PickupDeliveryCalculator extends BaseDeliveryCalculator<Void> implements DeliveryCalculator {

    public PickupDeliveryCalculator(
            final Order order,
            final Map<AmountTypes, BigDecimal> amounts,
            final GeoNamesTimezoneResponseDto toAddressTimezone) {
        super(order, amounts, toAddressTimezone, null);
    }

    @Override
    public DeliveryCalcParcelDto calc() {
        final BigDecimal totalAmount = amounts.get(AmountTypes.TOTAL);
        BigDecimal deliveryPrice = MOSCOW_PICKUP_DELIVERY_PRICE;
        if (totalAmount.compareTo(MIN_GOOD_MOSCOW_PARCEL_IS_FREE) > 0) {
            deliveryPrice = BigDecimal.ZERO;
        }
        final DeliveryCalcParcelDto result = new DeliveryCalcParcelDto();
        result.setDeliveryPrice(deliveryPrice);
        result.setDeliveryFullPrice(deliveryPrice);
        result.setDeliverySellerSummary(MOSCOW_PICKUP_DELIVERY_PRICE);
        result.setDeliveryCustomerSummary(deliveryPrice);
        result.setTo(order.getDelivery().getDeliveryType().getAnnotation());
        result.setTermText("сегодня");
        result.setWeightText(calcTotalWeightKg().toPlainString() + " кг.");
        return result;
    }
}
