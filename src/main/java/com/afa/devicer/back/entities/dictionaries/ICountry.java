package com.afa.devicer.back.entities.dictionaries;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ICountry extends JpaRepository<Country, UUID>, JpaSpecificationExecutor<Country> {

}
