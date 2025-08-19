package com.afa.devicer.back.dto.customers;

import com.afa.devicer.back.entities.dictionaries.Country;
import com.afa.devicer.back.enums.CustomerTypes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings({"PMD.CommentRequired", "PMD.TooManyFields"})
public class CustomerConditionsDto {

    @Schema(description = "customer id")
    private int id;

    @Schema(description = "list of customer types")
    private Set<CustomerTypes> customerTypes;

    @Schema(description = "list of countries")
    private Set<Country> countries;

    // CUSTOMER - PERSON
    private String personPhoneNumber;
    private String personLastName;
    private String personEmail;

    // CUSTOMER - COMPANY
    private String companyInn;
    private String companyShortName;
    private String companyMainContactPhoneNumber;
    private String companyMainContactEmail;

}
