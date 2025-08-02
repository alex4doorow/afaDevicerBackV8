package com.afa.devicer.back.exceptions;

import com.afa.devicer.back.enums.DevicerErrors;
import lombok.Getter;

import java.text.MessageFormat;

@Getter
public class DevicerException extends RuntimeException {

    private final String errorCode;
    private final String errorMessage;


    public DevicerException(final DevicerErrors error, final Object... args) {
        super(error.name());
        this.errorCode = error.name();
        this.errorMessage = (args == null || args.length == 0)
                ? error.getErrorMessage()
                : MessageFormat.format(error.getErrorMessage(), args);
    }
}
