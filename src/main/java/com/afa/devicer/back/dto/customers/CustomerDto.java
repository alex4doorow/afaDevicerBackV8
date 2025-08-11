package com.afa.devicer.back.dto.customers;

import com.afa.devicer.back.dto.companies.CompanyDto;
import com.afa.devicer.back.dto.persons.PersonFullDto;
import com.afa.devicer.back.dto.persons.PersonShortDto;
import com.afa.devicer.back.enums.CustomerTypes;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDto {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "customer type")
    private CustomerTypes type;

    @Schema(description = "company info")
    private CompanyDto company;

    @Schema(description = "person info")
    private PersonFullDto person;

    @Schema(description = "contacts")
    private Set<CustomerContactDto> contacts;

    @Schema(description = "addresses")
    private Set<CustomerAddressDto> addresses;

    @Schema(description = "автор")
    private PersonShortDto userAdded;

    @Schema(description = "создано", example = "2024-06-16T07:42:45Z")
    private Instant dateAdded;
}
