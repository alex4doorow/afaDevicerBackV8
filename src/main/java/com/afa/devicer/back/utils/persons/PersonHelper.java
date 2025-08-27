package com.afa.devicer.back.utils.persons;

import com.afa.core.dto.persons.PersonShortDto;
import com.afa.devicer.back.entities.people.Person;

@SuppressWarnings({"PMD.UseUtilityClass", "PMD.LawOfDemeter"})
public class PersonHelper {

    public static PersonShortDto fromPerson(final Person person) {
        return PersonShortDto.builder()
                .id(person.getId())
                .fullName(person.getFullName())
                .shortName(person.getShortName())
                .build();
    }
}
