package com.afa.devicer.back.entities.people;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface IEmployeeSetting extends
        JpaRepository<EmployeeSetting, UUID>,
        JpaSpecificationExecutor<EmployeeSetting> {

    Optional<EmployeeSetting> findByPersonId(final Long personId);

}
