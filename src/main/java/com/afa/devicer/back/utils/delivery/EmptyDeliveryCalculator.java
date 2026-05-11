package com.afa.devicer.back.utils.delivery;

import com.afa.core.dto.delivery.DeliveryCalcParcelDto;

public class EmptyDeliveryCalculator implements DeliveryCalculator {

    public EmptyDeliveryCalculator() {
        super();
    }

    @Override
    public DeliveryCalcParcelDto calc() {
        return DeliveryCalcParcelDto.createEmpty();
    }
}
