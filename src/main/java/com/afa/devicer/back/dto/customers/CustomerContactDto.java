package com.afa.devicer.back.dto.customers;

import com.afa.devicer.back.dto.persons.PersonFullDto;
import com.afa.devicer.back.enums.ContactTypes;
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
@Schema(description = "Customer contact")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerContactDto {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "customer contact type")
    private ContactTypes type;

    @Schema(description = "person")
    private PersonFullDto person;
}
