package com.afa.devicer.back.dto.customers;

import com.afa.devicer.back.dto.dictionaries.AddressDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer address")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerAddressDto {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "address")
    private AddressDto address;
}
