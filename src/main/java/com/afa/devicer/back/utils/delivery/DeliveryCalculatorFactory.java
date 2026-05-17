package com.afa.devicer.back.utils.delivery;

import com.afa.core.dto.integrations.geo.GeoNamesTimezoneResponseDto;
import com.afa.core.enums.AmountTypes;
import com.afa.core.enums.DeliveryTypes;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import com.afa.devicer.back.entities.orders.Order;
import com.afa.devicer.back.integrations.BaseConnector;
import com.afa.devicer.back.integrations.cdek.CdekApiConnector;
import com.afa.devicer.back.integrations.post.PostCalcApiConnector;

import java.math.BigDecimal;
import java.util.Map;

@SuppressWarnings({"PMD.LawOfDemeter"})
public final class DeliveryCalculatorFactory {

    private DeliveryCalculatorFactory() {
    }

    public static DeliveryCalculator createCalculator(
            final Order order,
            final Map<AmountTypes, BigDecimal> amounts,
            final GeoNamesTimezoneResponseDto toAddressTimezone,
            final BaseConnector connector) {

        final DeliveryTypes deliveryType = order.getDelivery().getDeliveryType();

        if (deliveryType == DeliveryTypes.COURIER_MOSCOW_TYPICAL) {
            return new MoscowCourierTypicalDeliveryCalculator(order, amounts, toAddressTimezone);
        } else if (deliveryType == DeliveryTypes.COURIER_MOSCOW_FAST) {
            return new MoscowCourierFastDeliveryCalculator(order, amounts, toAddressTimezone);
        } else if (deliveryType == DeliveryTypes.COURIER_MO_TYPICAL) {
            return new MoCourierTypicalDeliveryCalculator(order, amounts, toAddressTimezone);
        } else if (deliveryType == DeliveryTypes.PICKUP) {
            return new PickupDeliveryCalculator(order, amounts, toAddressTimezone);
        } else if (deliveryType == DeliveryTypes.DELLIN) {
            return new DellinDeliveryCalculator(order, amounts, toAddressTimezone);
        } else if (deliveryType.isPost()) {
            return new PostDeliveryCalculator(order,
                    amounts,
                    toAddressTimezone,
                    requirePostCalcConnector(connector));
        } else if (deliveryType.isCdek()) {
            return new CdekDeliveryCalculator(
                    order,
                    amounts,
                    toAddressTimezone,
                    requireCdekConnector(connector)
            );
        }

        return new EmptyDeliveryCalculator();
    }

    private static CdekApiConnector requireCdekConnector(final BaseConnector connector) {
        if (connector instanceof CdekApiConnector cdekApiConnector) {
            return cdekApiConnector;
        }
        throw new DevicerException(DevicerErrors.UNKNOWN_ILLEGAL_ARGUMENT_ERROR,
                connector == null ? "null" : connector.getClass().getName(),
                CdekApiConnector.class.getName());
    }

    private static PostCalcApiConnector requirePostCalcConnector(final BaseConnector connector) {
        if (connector instanceof PostCalcApiConnector postCalcApiConnector) {
            return postCalcApiConnector;
        }
        throw new DevicerException(DevicerErrors.UNKNOWN_ILLEGAL_ARGUMENT_ERROR,
                connector == null ? "null" : connector.getClass().getName(),
                PostCalcApiConnector.class.getName());
    }
}
