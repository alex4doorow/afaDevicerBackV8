package com.afa.devicer.back.dto;

import com.afa.devicer.back.components.ExcludeFromJacocoGeneratedReport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;

import java.util.ArrayList;
import java.util.List;

@Getter
@ExcludeFromJacocoGeneratedReport
public class ValidationErrorResponse {
    @Schema(description = "operation result", allowableValues = "error")
    private final String result;

    @Schema(description = "operation uri", example = "/login")
    private final String uri;

    @Schema(description = "operation method", example = "POST")
    private final String method;

    @Schema(description = "error's list if exists")
    private final List<ValidationErrorViolation> violations = new ArrayList<>();


    /**
     * Constructs new instance of check validation response.
     *
     * @param uri - Request URI
     */
    public ValidationErrorResponse(final String uri, final String method) {
        this.result = "error";
        this.uri = uri;
        this.method = method;
    }

    /**
     * Constructs new instance of check validation response.
     *
     * @param uri    - Request URI
     * @param errors - error list
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public <T> ValidationErrorResponse(final String uri, final String method, final List<T> errors) {
        this(uri, method);
        for (final T error : errors) {
            switch (error) {
                case FieldError fe ->
                        this.violations.add(new ValidationErrorViolation(fe.getField(), fe.getDefaultMessage()));
                case ParameterValidationResult pvr -> pvr.getResolvableErrors().stream()
                        .filter(msr -> msr.getArguments() != null && msr.getArguments().length > 0)
                        .forEach(msr -> {
                            final String field = ((DefaultMessageSourceResolvable) msr.getArguments()[0]).getDefaultMessage();
                            final String message = msr.getDefaultMessage();
                            this.violations.add(new ValidationErrorViolation(field, message));
                        });
                default -> { }
            }
        }
    }


    @ExcludeFromJacocoGeneratedReport
    private record ValidationErrorViolation(String field, String error) { }
}
