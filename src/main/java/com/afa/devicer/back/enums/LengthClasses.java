package com.afa.devicer.back.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LengthClasses {

    MILLIMETER("Миллиметр"),
    CENTIMETER("Сантиметр");

    private final String annotation;
}
