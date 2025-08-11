package com.afa.devicer.back.dto.companies;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Company")
public class CompanySaveRequest {

    @NotBlank
    @Schema(description = "inn", minLength = 10, maxLength = 12)
    private String inn;

    @NotBlank
    @Size(min = 2, max = 250)
    @Schema(description = "short name")
    private String shortName;

    @Schema(description = "long name")
    private String longName;

    @Size(min = 15, max = 15)
    @Schema(description = "phone_number", example = "(111) 111-11-11")
    private String phoneNumber;

    @NotNull
    @Size(min = 7, max = 250)
    @Schema(description = "email")
    private String email;
}
