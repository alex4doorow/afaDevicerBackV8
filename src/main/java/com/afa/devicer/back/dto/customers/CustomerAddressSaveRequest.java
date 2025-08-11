package com.afa.devicer.back.dto.customers;

import com.afa.devicer.back.dto.dictionaries.AddressSaveRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer address")
public class CustomerAddressSaveRequest {


    @NotNull
    @Schema(description = "person")
    private AddressSaveRequest address;
}
