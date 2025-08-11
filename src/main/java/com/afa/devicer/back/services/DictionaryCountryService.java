package com.afa.devicer.back.services;

import com.afa.devicer.back.entities.dictionaries.Country;
import com.afa.devicer.back.entities.dictionaries.Country_;
import com.afa.devicer.back.entities.dictionaries.ICountry;
import com.afa.devicer.back.enums.DevicerErrors;
import com.afa.devicer.back.exceptions.DevicerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DictionaryCountryService {

    private final ICountry iCountry;

    public Optional<Country> findByIdOptional(final UUID id) {
        return iCountry.findById(id);
    }

    public Country findByIdOrThrow(final UUID id) {
        return findByIdOptional(id).orElseThrow(() ->
                new DevicerException(DevicerErrors.DB_ENTITY_NOT_FOUND, Country_.class_, id)
        );
    }
}
