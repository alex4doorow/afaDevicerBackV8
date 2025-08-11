package com.afa.devicer.back.services;

import com.afa.devicer.back.entities.people.IPerson;
import com.afa.devicer.back.entities.people.Person;
import com.afa.devicer.back.entities.people.Person_;
import com.afa.devicer.back.enums.DevicerErrors;
import com.afa.devicer.back.exceptions.DevicerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final IPerson iPerson;

    public Optional<Person> findByIdOptional(final Long id) {
        return iPerson.findById(id);
    }

    public Person findByIdOrThrow(final Long id) {
        return findByIdOptional(id).orElseThrow(() ->
                new DevicerException(DevicerErrors.DB_ENTITY_NOT_FOUND, Person_.class_, id)
        );
    }

    public Person findByPhoneNumber(final String phoneNumber) {
        final Optional<Person> result = iPerson.findByPhoneNumber(phoneNumber);
        return result.orElse(null);
    }

    public Person create(final Person person) {
        return iPerson.save(person);
    }

}
