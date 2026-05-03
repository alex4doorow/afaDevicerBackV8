package com.afa.devicer.back.services;

import com.afa.core.dto.dictionaries.CountryDto;
import com.afa.core.dto.dictionaries.CountryFilter;
import com.afa.devicer.back.config.CacheConfig;
import com.afa.devicer.back.entities.dictionaries.Country;
import com.afa.devicer.back.entities.dictionaries.Country_;
import com.afa.devicer.back.entities.dictionaries.ICountry;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import com.afa.devicer.back.mappers.CountryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DictionaryCountryService {

    private final ICountry iCountry;
    private final CountryMapper mapper;

    public Optional<Country> findByIdOptional(final UUID id) {
        return iCountry.findById(id);
    }

    public Country findByIdOrThrow(final UUID id) {
        return findByIdOptional(id).orElseThrow(() ->
                new DevicerException(DevicerErrors.DB_ENTITY_NOT_FOUND, Country_.class_, id)
        );
    }

    @Cacheable(cacheNames = CacheConfig.CACHE_COUNTRY)
    @Transactional(readOnly = true)
    public List<CountryDto> getFiltered(final CountryFilter filter) {
        return iCountry.findAllActive().stream()
                .map(mapper::fromCountry)
                .toList();
    }
}
