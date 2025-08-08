package com.afa.devicer.back.enums;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum DeliveryPriceTypes {
	
	UNKNOWN(DeliveryTypes.UNKNOWN, "", BigDecimal.ZERO),
	COURIER_MOSCOW_TYPICAL(DeliveryTypes.COURIER_MOSCOW_TYPICAL, "Москва, обычный", BigDecimal.valueOf(300)),
	COURIER_MOSCOW_FAST(DeliveryTypes.COURIER_MOSCOW_FAST, "Москва, срочный", BigDecimal.valueOf(500)),
	
	COURIER_MO_TYPICAL_MKAD_15_KM(DeliveryTypes.COURIER_MO_TYPICAL, "область, <= 15 км", BigDecimal.valueOf(500)),
	COURIER_MO_TYPICAL_MKAD_16_35_KM(DeliveryTypes.COURIER_MO_TYPICAL, "область, 16-35 км", BigDecimal.valueOf(800)),
	COURIER_MO_TYPICAL_MKAD_36_60_KM(DeliveryTypes.COURIER_MO_TYPICAL, "область, 36-60 км", BigDecimal.valueOf(1300)),
	COURIER_MO_TYPICAL_MKAD_61_100_KM(DeliveryTypes.COURIER_MO_TYPICAL, "область, 61-100 км", BigDecimal.valueOf(1900));
	
	private final DeliveryTypes deliveryType;
	private final String annotation;
	private final BigDecimal price;
	
	DeliveryPriceTypes(final DeliveryTypes deliveryType, final String annotation, final BigDecimal price) {
		this.deliveryType = deliveryType;
		this.annotation = annotation;
		this.price = price;		
	}

}
