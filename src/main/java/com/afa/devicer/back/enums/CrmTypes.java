package com.afa.devicer.back.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CrmTypes {

    NONE("нет"),
    OPENCART("Админка сайта"),
    EMAIL("Быстрй заказ"),
    CDEK("СДЭК Маркет"),
    YANDEX_MARKET("Яндекс Маркет"),
    OZON("ОЗОН Маркет");

    private final String name;
}
