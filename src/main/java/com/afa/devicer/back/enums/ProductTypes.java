package com.afa.devicer.back.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductTypes {
	
	/**
	 * [100] Товары которые мы продаем
	 */
	MAIN("основной"),
	/**
	 * [101] Дополнения или услуги которые мы тоже продаем (запчасти, флагштоки, поверка и т.п.)
	 */
	ADDITIONAL("дополнительный");

    private final String name;
}
