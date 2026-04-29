package com.afa.devicer.back.entities.people;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface IPersonSetting extends
        JpaRepository<PersonSetting, UUID>,
        JpaSpecificationExecutor<PersonSetting> {

    Optional<PersonSetting> findByPersonId(final Long personId);

}
