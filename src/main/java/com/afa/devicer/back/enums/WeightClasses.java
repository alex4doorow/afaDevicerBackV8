package com.afa.devicer.back.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WeightClasses {

    KILOGRAM("Килограмм"),
    GRAM("Грамм");

    private final String annotation;
}
