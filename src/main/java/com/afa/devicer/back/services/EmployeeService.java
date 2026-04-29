package com.afa.devicer.back.services;

import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import com.afa.devicer.back.entities.people.Employee;
import com.afa.devicer.back.entities.people.Employee_;
import com.afa.devicer.back.entities.people.IEmployee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings({"PMD.LawOfDemeter"})
public class EmployeeService {

    private final IEmployee iEmployee;

    public Optional<Employee> findByOptionalKeycloakId(final UUID keycloakId) {
        return iEmployee.findByPersonKeycloakUuid(keycloakId);
    }

    @Transactional(readOnly = true)
    public Employee findByKeycloakIdOrThrow(final UUID keycloakId) {
        return findByOptionalKeycloakId(keycloakId).orElseThrow(() ->
                new DevicerException(DevicerErrors.DB_ENTITY_NOT_FOUND, Employee_.class_, keycloakId)
        );
    }
}
