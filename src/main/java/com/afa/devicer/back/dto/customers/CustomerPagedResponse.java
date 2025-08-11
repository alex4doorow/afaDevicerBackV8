package com.afa.devicer.back.dto.customers;

import com.afa.devicer.back.dto.BasePagedResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@Schema(description = "customer response")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerPagedResponse extends BasePagedResponse {

    @Schema(description = "customer list with all data")
    private final List<CustomerDto> orders;

    public CustomerPagedResponse(
            final long totalElements, final int totalPages,
            final boolean hasPrev, final boolean hasNext,
            final List<CustomerDto> orders) {
        super(totalElements, totalPages, hasPrev, hasNext);
        this.orders = orders;
    }
}