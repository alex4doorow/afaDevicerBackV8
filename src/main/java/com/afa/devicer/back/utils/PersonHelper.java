package com.afa.devicer.back.utils;

import com.afa.core.dto.people.PersonShortDto;
import com.afa.devicer.back.entities.people.Person;

public final class PersonHelper {

    private PersonHelper() {
    }

    public static PersonShortDto fromPerson(final Person person) {
        return PersonShortDto.builder()
                .id(person.getId())
                .fullName(person.getFullName())
                .shortName(person.getShortName())
                .build();
    }
}
