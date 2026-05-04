package com.afa.devicer.back.services;

import com.afa.devicer.back.entities.dictionaries.Address;
import com.afa.devicer.back.entities.dictionaries.IAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {

    private final IAddress iAddress;

    @Transactional
    public Address create(final Address address) {
        return iAddress.save(address);
    }

}
