package com.afa.devicer.back.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderEmailStatusTypes {
	
	FEEDBACK("запрос отзыва"),
	TERM_EXPAIRED("запрос на актуальность");
		
	private final String annotation;
}
