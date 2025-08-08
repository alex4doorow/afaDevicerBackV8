package com.afa.devicer.back.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CrmStatuses {

    NONE("нет обработки"),
    SUCCESS("успешно"),
    FAIL("ошибки");

    private final String name;

}
