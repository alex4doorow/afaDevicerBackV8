package com.afa.devicer.back.dto;

import com.afa.devicer.back.components.ExcludeFromJacocoGeneratedReport;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ExcludeFromJacocoGeneratedReport
public class BasePagedResponse {
    @Schema(description = "Total element count", example = "1000")
    private long totalElements;

    @Schema(description = "Total page count", example = "25")
    private int totalPages;

    @Schema(description = "Is previous page exists", allowableValues = "true,false")
    private boolean hasPrev;

    @Schema(description = "Is next page exists", allowableValues = "true,false")
    private boolean hasNext;
}
