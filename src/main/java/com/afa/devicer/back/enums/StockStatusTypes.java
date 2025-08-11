package com.afa.devicer.back.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StockStatusTypes {

    IN_STOCK("В наличии"),
    PRE_ORDER("Предзаказ"),
    OUT_OF_STOCK("Нет в наличии"),
    WAITING_2_3_DAYS("Ожидание 2-3 дня");

    private final String annotation;
}
