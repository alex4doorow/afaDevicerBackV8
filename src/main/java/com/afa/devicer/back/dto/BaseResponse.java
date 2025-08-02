package com.afa.devicer.back.dto;

import com.afa.devicer.back.components.ExcludeFromJacocoGeneratedReport;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ExcludeFromJacocoGeneratedReport
public class BaseResponse {
    @Schema(description = "operation result", allowableValues = "ok,error")
    private final String result;

    @Schema(description = "error code if exists", example = "FILE_NOT_FOUND")
    private String errorCode;

    @Schema(description = "error message if exists", example = "clientId cannot be null")
    private String errorMessage;


    /**
     * Create OK response, used for check save request
     */
    public BaseResponse() {
        this.result = "ok";
    }

    /**
     * Create error response for invalid request
     *
     * @param errorMessage - Error description message
     */
    public BaseResponse(final String errorMessage) {
        this.result = "error";
        this.errorMessage = errorMessage;
    }

    /**
     * Create error response for invalid request
     *
     * @param errorCode - Error code
     * @param errorMessage - Error description message
     */
    public BaseResponse(final String errorCode, final String errorMessage) {
        this.result = "error";
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
