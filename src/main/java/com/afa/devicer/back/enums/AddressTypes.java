package com.afa.devicer.back.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AddressTypes {
		
	MAIN(1, "основной"),
	ADDITIONAL(2, "дополнительный"),
	UNKNOWN(3, "неизвестный");

	private final int id;
    private final String annotation;

}
