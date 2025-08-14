package com.afa.devicer.back.dto.dictionaries;

import com.afa.devicer.back.enums.AddressTypes;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Address")
public class AddressSaveRequest {

    @NotNull
    @Schema(description = "country id")
    private UUID countryId;

    @NotNull
    @Schema(description = "address type")
    private AddressTypes type;

    @Size(min = 6, max = 6)
    @Schema(description = "post code")
    private String postCode;

    @Size(min = 2, max = 256)
    @Schema(description = "street")
    private String street;

    @Size(min = 1, max = 128)
    @Schema(description = "house")
    private String house;

    @Size(min = 1, max = 128)
    @Schema(description = "flat")
    private String flat;

    @NotBlank
    @Size(min = 2, max = 250)
    @Schema(description = "address line")
    private String addressLine;
}
