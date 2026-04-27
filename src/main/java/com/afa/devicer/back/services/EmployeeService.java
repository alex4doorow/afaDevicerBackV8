package com.afa.devicer.back.services;

import com.afa.core.dto.employee.EmployeeSettingsDto;
import com.afa.core.dto.employee.EmployeeSettingsSaveRequest;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import com.afa.devicer.back.entities.people.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final IEmployee iEmployee;
    private final IEmployeeSetting iEmployeeSetting;
    private final ObjectMapper objectMapper;

    public Optional<Employee> findByOptionalKeycloakId(final UUID keycloakId) {
        return iEmployee.findByPersonKeycloakUuid(keycloakId);
    }

    @Transactional(readOnly = true)
    public Employee findByKeycloakIdOrThrow(final UUID keycloakId) {
        return findByOptionalKeycloakId(keycloakId).orElseThrow(() ->
                new DevicerException(DevicerErrors.DB_ENTITY_NOT_FOUND, Employee_.class_, keycloakId)
        );
    }

    @Transactional(readOnly = true)
    public EmployeeSettingsDto getSettings(final UUID keycloakId) {
        final Employee employee = findByKeycloakIdOrThrow(keycloakId);

        return iEmployeeSetting.findByPersonId(employee.getId())
                .map(setting -> objectMapper.convertValue(
                        setting.getData(),
                        EmployeeSettingsDto.class
                ))
                .orElseGet(EmployeeSettingsDto::new);
    }

    @Transactional
    public void saveSettings(final UUID keycloakId, final EmployeeSettingsSaveRequest request) {
        final Employee employee = findByKeycloakIdOrThrow(keycloakId);

        final EmployeeSetting setting = iEmployeeSetting
                .findByPersonId(employee.getPerson().getId())
                .orElseGet(() -> {
                    final EmployeeSetting newSetting = new EmployeeSetting();
                    newSetting.setPerson(employee.getPerson());
                    return newSetting;
                });


        final Map<String, Object> jsonData = objectMapper.convertValue(
                request.getSettings() == null ? new EmployeeSettingsDto() : request.getSettings(),
                new TypeReference<Map<String, Object>>() {
                }
        );

        setting.setData(jsonData);
        iEmployeeSetting.save(setting);
    }
}
