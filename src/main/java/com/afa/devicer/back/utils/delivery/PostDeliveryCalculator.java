package com.afa.devicer.back.utils.delivery;

import com.afa.core.dto.delivery.DeliveryCalcParcelDto;
import com.afa.core.dto.integrations.geo.GeoNamesTimezoneResponseDto;
import com.afa.core.enums.AmountTypes;
import com.afa.devicer.back.entities.orders.Order;

import java.math.BigDecimal;
import java.util.Map;

public class PostDeliveryCalculator extends BaseDeliveryCalculator<Void> implements DeliveryCalculator {

    public PostDeliveryCalculator(
            final Order order,
            final Map<AmountTypes, BigDecimal> amounts,
            final GeoNamesTimezoneResponseDto toAddressTimezone) {
        super(order, amounts, toAddressTimezone, null);
    }

    @Override
    public DeliveryCalcParcelDto calc() {
             /*
            try {
                result = postCalc(order, totalAmount, deliveryType, to);
                GeoNamesApi.GeoNamesBean geoNamesBean = geoNamesApi.getLocalTimeByCity(to.getAddress(), new Date());
                result.setLocalTimeText(geoNamesBean.textLocalTime());
                return result;

            } catch (IOException e) {
                log.error("IOException", e);
            }
            */
        return DeliveryCalcParcelDto.createEmpty();
    }
}
