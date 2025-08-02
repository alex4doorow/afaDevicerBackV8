package com.afa.devicer.back.components;

import org.springframework.boot.autoconfigure.security.SecurityProperties;

@SuppressWarnings({ "PMD.DataClass" })
public class FilterOrderConstants {
    // Чем меньше значение, тем раньше срабатывает фильтр
    public static final int DEACTIVATED_USER_FILTER = SecurityProperties.DEFAULT_FILTER_ORDER + 10;
    public static final int LOGGING_FILTER = SecurityProperties.DEFAULT_FILTER_ORDER + 20;
}
