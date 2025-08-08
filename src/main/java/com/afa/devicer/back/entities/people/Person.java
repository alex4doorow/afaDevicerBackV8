package com.afa.devicer.back.entities.people;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "bp_person",
        indexes = {
                @Index(name = "idx_bp_person_keycloak_uuid", columnList = "keycloak_uuid")
        })
public class Person {

    @Id
    @NotNull
    @SequenceGenerator(name = "d_sequence", sequenceName = "d_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "keycloak_uuid")
    private UUID keycloakUuid;

    @NotNull
    @Column(name = "first_name", nullable = false, length = 256)
    private String firstName;

    @Column(name = "last_name", length = 256)
    private String lastName;

    @Column(name = "middle_name", length = 256)
    private String middleName;

    @Column(name = "iso_code_3", length = 3)
    private String isoCode3;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "phone_number", nullable = false, length = 256)
    private String phoneNumber;

    @Column(name = "email", length = 256)
    private String email;

//    @NotNull
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "person", cascade = CascadeType.ALL)
//    @EqualsAndHashCode.Exclude
//    private List<PersonContact> contacts = new ArrayList<>();

    @NotNull
    @Column(name = "rec_status", nullable = false)
    private Character recStatus;

    @NotNull
    @Column(name = "date_added", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateAdded = Instant.now();

    @Column(name = "date_modified", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateModified;

    public String getFullName() {
        return (lastName + " " + firstName + " " + middleName).trim();
    }

    public String getShortName() {
        final String firstInitial = firstName.isEmpty() ? "" : firstName.charAt(0) + ".";
        return (lastName + " " + firstInitial).trim();
    }

    public Boolean getDeactivated() {
        return Character.valueOf('D').equals(recStatus);
    }
}