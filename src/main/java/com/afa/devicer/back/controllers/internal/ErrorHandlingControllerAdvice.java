package com.afa.devicer.back.controllers.internal;

import com.afa.core.dto.BaseResponse;
import com.afa.core.dto.ValidationErrorResponse;
import com.afa.core.exceptions.DevicerException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@ControllerAdvice
@NoArgsConstructor
public class ErrorHandlingControllerAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ValidationErrorResponse handle(final HttpServletRequest request, final HandlerMethodValidationException ex) {
        return new ValidationErrorResponse(request.getRequestURI(), request.getMethod(), ex.getAllErrors());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BindException.class)
    public ValidationErrorResponse handle(final HttpServletRequest request, final BindException ex) {
        return new ValidationErrorResponse(request.getRequestURI(), request.getMethod(), ex.getFieldErrors());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrorResponse handle(final HttpServletRequest request, final MethodArgumentNotValidException ex) {
        return new ValidationErrorResponse(request.getRequestURI(), request.getMethod(), ex.getFieldErrors());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(InvalidFormatException.class)
    public BaseResponse handle(final InvalidFormatException ex) {
        return new BaseResponse(ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(IllegalArgumentException.class)
    public BaseResponse handle(final IllegalArgumentException ex) {
        return new BaseResponse(ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(DevicerException.class)
    public BaseResponse handle(final DevicerException ex) {
        return new BaseResponse(ex.getErrorCode(), ex.getErrorMessage());
    }

    // Don't handle RuntimeException or Exception. It cancels standard errors like 403
    /*
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse handle(final RuntimeException ex) {
        return new BaseResponse(ex.getMessage());
    }
    */
}
