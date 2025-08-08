package com.afa.devicer.back.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentDeliveryTypes {

    CUSTOMER("покупатель"),
    SELLER("продавец");

    private final String annotation;

}
