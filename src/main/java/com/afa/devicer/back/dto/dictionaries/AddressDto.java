package com.afa.devicer.back.dto.dictionaries;

import com.afa.devicer.back.enums.AddressTypes;
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
@Schema(description = "Address")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDto {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "address type")
    private AddressTypes type;

    @Schema(description = "post code")
    private String postCode;

    @Schema(description = "street")
    private String street;

    @Schema(description = "house")
    private String house;

    @Schema(description = "flat")
    private String flat;

    @Schema(description = "address line")
    private String addressLine;
}
