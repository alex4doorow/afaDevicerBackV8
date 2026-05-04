package com.afa.devicer.back.services;

import com.afa.core.dto.people.PersonSettingsDto;
import com.afa.core.dto.people.PersonSettingsSaveRequest;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import com.afa.devicer.back.entities.people.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"PMD.LawOfDemeter"})
public class PersonService {

    private final IPerson iPerson;
    private final IPersonSetting iPersonSetting;
    private final ObjectMapper objectMapper;

    public Optional<Person> findByIdOptional(final Long id) {
        return iPerson.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Person> findByOptionalKeycloakId(final UUID keycloakId) {
        return iPerson.findByKeycloakUuid(keycloakId);
    }

    @Transactional(readOnly = true)
    public Person findByKeycloakIdOrThrow(final UUID keycloakId) {
        return findByOptionalKeycloakId(keycloakId).orElseThrow(() ->
                new DevicerException(DevicerErrors.DB_ENTITY_NOT_FOUND, Person_.class_, keycloakId)
        );
    }

    @Transactional(readOnly = true)
    public Optional<Person> findByPhoneNumberOptional(final String phoneNumber) {
        return iPerson.findByPhoneNumber(phoneNumber);
    }

    @Transactional(readOnly = true)
    public Person findByIdOrThrow(final Long id) {
        return findByIdOptional(id).orElseThrow(() ->
                new DevicerException(DevicerErrors.DB_ENTITY_NOT_FOUND, Person_.class_, id)
        );
    }

    @Transactional
    public Person create(final Person person) {
        return iPerson.save(person);
    }

    @Transactional(readOnly = true)
    public PersonSettingsDto getSettings(final UUID keycloakId) {
        final Person person = findByKeycloakIdOrThrow(keycloakId);

        return iPersonSetting.findByPersonId(person.getId())
                .map(setting -> objectMapper.convertValue(
                        setting.getData(),
                        PersonSettingsDto.class
                ))
                .orElseGet(PersonSettingsDto::new);
    }

    @Transactional
    public void saveSettings(final UUID keycloakId, final PersonSettingsSaveRequest request) {
        final Person person = findByKeycloakIdOrThrow(keycloakId);

        final PersonSetting setting = iPersonSetting
                .findByPersonId(person.getId())
                .orElseGet(() -> {
                    final PersonSetting newSetting = new PersonSetting();
                    newSetting.setPerson(person);
                    return newSetting;
                });

        final Map<String, Object> jsonData = objectMapper.convertValue(
                request.getSettings() == null ? new PersonSettingsDto() : request.getSettings(),
                new TypeReference<Map<String, Object>>() {
                }
        );

        setting.setData(jsonData);
        iPersonSetting.save(setting);
    }

}
