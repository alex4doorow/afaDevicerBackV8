package com.afa.devicer.back.mappers.dictionaries;

import com.afa.core.dto.dictionaries.CountryDto;
import com.afa.devicer.back.entities.dictionaries.Country;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CountryMapper {

    CountryDto fromCountry(Country entity);

}
