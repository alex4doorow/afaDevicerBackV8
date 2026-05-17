package com.afa.devicer.back.utils.delivery;

import com.afa.core.dto.delivery.DeliveryCalcParcelDto;

import java.math.BigDecimal;

public class EmptyDeliveryCalculator implements DeliveryCalculator {

    public EmptyDeliveryCalculator() {
        super();
    }

    @Override
    public DeliveryCalcParcelDto calc() {
        final DeliveryCalcParcelDto result = DeliveryCalcParcelDto.createEmpty();
        result.setDeliveryAmount(BigDecimal.valueOf(300));
        return result;
    }
}
