package com.afa.devicer.back.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderTypes {

    ORDER("заказ"),
    BILL("счет на оплату"),
    KP("коммерческое предложение"),
    CONSULTATION("консультация"),
    CHANGE("замена"),
    REFUND("возврат"),
    GIFT("подарок"),
    TEST_DIVE("тест-драйв"),
    REPAIR("ремонт");

	private final String annotation;
}
