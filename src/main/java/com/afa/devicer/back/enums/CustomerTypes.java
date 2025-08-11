package com.afa.devicer.back.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomerTypes {

    PERSON("физическое лицо", "Физ. лицо"),
    COMPANY("юридическое лицо", "Юр. лицо"),
    BUSINESSMAN("индивидуальный предприниматель", "ИП"),
    FOREIGNER_PERSON("нерезидент, физическое лицо", "Нерезидент ФЛ"),
    FOREIGNER_COMPANY("нерезидент, юридическое лицо", "Нерезидент ЮЛ"),
    UNKNOWN("нечто", "нечто");

    private final String longName;
    private final String shortName;

}
