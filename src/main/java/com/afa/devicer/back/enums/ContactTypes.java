package com.afa.devicer.back.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContactTypes {
		
	MAIN("основной"),
	ADDITIONAL("дополнительный");

    private final String annotation;
}
