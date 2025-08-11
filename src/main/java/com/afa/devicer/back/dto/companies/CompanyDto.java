package com.afa.devicer.back.dto.companies;

import com.afa.devicer.back.dto.dictionaries.CountryDto;
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
@Schema(description = "Company")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyDto {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "country")
    private CountryDto country;

    @Schema(description = "inn")
    private String inn;

    @Schema(description = "short name")
    private String shortName;

    @Schema(description = "long name")
    private String longName;

    @Schema(description = "phone_number")
    private String phoneNumber;

    @Schema(description = "email")
    private String email;
}
