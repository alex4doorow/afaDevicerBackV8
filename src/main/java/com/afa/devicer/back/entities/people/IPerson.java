package com.afa.devicer.back.entities.people;

import com.afa.devicer.back.dto.UserInfoDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IPerson extends JpaRepository<Person, Long> {

    Optional<Person> findByKeycloakUuid(UUID keycloakUuid);

    Optional<Person> findByPhoneNumber(String phoneNumber);

    @Query("SELECT p FROM Person p WHERE p.keycloakUuid = :keycloakUuid AND p.id <> :id")
    Optional<Person> findByKeycloakUuidExcludingId(@Param("keycloakUuid") UUID keycloakUuid, @Param("id") Long id);

    @Query("""
        select  person.id as personId,
                person.firstName as firstName,
                person.middleName as middleName,
                person.lastName as lastName,
                trim(person.lastName || ' ' || person.firstName || ' ' || person.middleName) as fullName,
                person.recStatus as recStatus,
                e.id as employeeId
            from Person person
            left join Employee e on e.person = person
               where person.keycloakUuid = :keycloakUuid
        """)
    Optional<UserInfoDbModel> fillUserInfo(@Param("keycloakUuid") UUID keycloakUuid);

/*
    @Query("""
        select  person.id as personId,
                person.firstName as firstName,
                person.middleName as middleName,
                person.lastName as lastName,
                trim(person.lastName || ' ' || person.firstName || ' ' || person.middleName) as fullName,
                person.deactivated as deactivated,
                e.id as employeeId, e.bossFlag as bossFlag,
                d.id as departmentId, d.name as departmentName,
                d.commerceFlag as commerceFlag,
                d.resourceFlag as resourceFlag,
                d.managementFlag as managementFlag,
                pr.id as partnerRepresentativeId,
                partner.id as partnerId,
                partner.alias as partnerAlias
            from Person person
            left join Employee e on e.person = person
            left join Department d on e.department = d
            left join PartnerRepresentative pr on pr.person = person
            left join Partner partner on pr.partner = partner
            where person.keycloakUuid = :keycloakUuid
        """)
    Optional<UserInfoDbModel> fillUserInfo(@Param("keycloakUuid") UUID keycloakUuid);
*/
}

