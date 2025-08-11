package com.afa.devicer.back.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderAdvertTypes {
	
	UNKNOWN("неопределен"),
	ADVERT("реклама"),
	CONTEXT("поиск"),
	YOUTUBE("youTube"),
	LOYALTY("сарафанное радио"),
	REPEAT_CALL("повторное обращение"),
	COLD_CALL("холодный звонок"),
	TENDER("тендер"),
	YANDEX_MARKET("яндекс.маркет"),
	OZON("озон");
		
	private final String annotation;

}
