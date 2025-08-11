package com.afa.devicer.back.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StoreTypes {
	
	/**
	 * pribormaster.ru
	 */
	PM("pribormaster.ru", "info@pribormaster.ru", "ПРИБОРМАСТЕР", "pm");
		
	private final String site;
    private final String email;
    private final String annotation;
    private final String prefix;
}
