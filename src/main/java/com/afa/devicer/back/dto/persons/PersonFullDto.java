package com.afa.devicer.back.dto.persons;

import com.afa.devicer.back.dto.dictionaries.CountryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "person full info")
public class PersonFullDto {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "country")
    private CountryDto country;

    @Schema(description = "first name", example = "Сергей")
    private String firstName;

    @Schema(description = "last name", example = "Туманов")
    private String lastName;

    @Schema(description = "last name", example = "V")
    private String middleName;

    @Schema(description = "phone_number")
    private String phoneNumber;

    @Schema(description = "email")
    private String email;
}
