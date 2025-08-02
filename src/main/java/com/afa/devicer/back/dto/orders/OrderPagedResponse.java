package com.afa.devicer.back.dto.orders;

import com.afa.devicer.back.dto.BasePagedResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@Schema(description = "vacancies response")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderPagedResponse extends BasePagedResponse {

    @Schema(description = "order list with all data")
    private final List<OrderDto> orders;

    public OrderPagedResponse(
            final long totalElements, final int totalPages,
            final boolean hasPrev, final boolean hasNext,
            final List<OrderDto> orders) {
        super(totalElements, totalPages, hasPrev, hasNext);
        this.orders = orders;
    }
}