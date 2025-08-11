package com.afa.devicer.back.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderPaymentTypes {

	PREPAYMENT("предоплата"),
	POSTPAY("постоплата"),
	PAYMENT_COURIER("наличными курьеру"),
	YANDEX_PAY("банковской картой");

	private final String annotation;
}
