package com.afa.devicer.back.utils.delivery;

import com.afa.core.dto.delivery.DeliveryCalcParcelDto;

import java.math.BigDecimal;

@SuppressWarnings({"PMD.LawOfDemeter", "PMD.AvoidBranchingStatementAsLastInLoop"})
public interface DeliveryCalculator {

    BigDecimal MOSCOW_PARCEL_DELIVERY_PRICE = BigDecimal.valueOf(200);
    BigDecimal MOSCOW_PICKUP_DELIVERY_PRICE = BigDecimal.valueOf(170);
    BigDecimal MIN_GOOD_MOSCOW_PARCEL_IS_FREE = BigDecimal.valueOf(3000);
    BigDecimal MIN_GOOD_MOSCOW_COURIER_IS_FREE = BigDecimal.valueOf(10000);

    DeliveryCalcParcelDto calc();
}
