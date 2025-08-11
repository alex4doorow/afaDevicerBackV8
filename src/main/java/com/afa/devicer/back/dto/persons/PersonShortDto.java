package com.afa.devicer.back.dto.persons;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "person short info")
public class PersonShortDto {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "full name", example = "Ёжиков-Туманов Сергей Владимирович")
    private String fullName;

    @Schema(description = "short name", example = "Ёжиков-Туманов С.В.")
    private String shortName;
}
