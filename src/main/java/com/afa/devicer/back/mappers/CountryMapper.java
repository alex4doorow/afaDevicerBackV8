package com.afa.devicer.back.mappers;

import com.afa.core.dto.dictionaries.CountryDto;
import com.afa.devicer.back.entities.dictionaries.Country;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@SuppressWarnings({"PMD.LawOfDemeter"})
public interface CountryMapper {

    CountryDto fromCountry(Country entity);


}
