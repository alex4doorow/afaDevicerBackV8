package com.afa.devicer.back.entities.people;

import com.afa.devicer.back.entities.dictionaries.Country;
import com.afa.core.utils.DefaultConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bp_persons",
        indexes = {
                @Index(name = "idx_bp_persons_keycloak_uuid", columnList = "keycloak_uuid"),
                @Index(name = "uq_bp_persons_phone_number", columnList = "phone_number", unique = true)
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
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Country country;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @NotNull
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @NotNull
    @Builder.Default
    @Column(name = "rec_status", nullable = false)
    private Character recStatus = DefaultConstants.ACTIVE;

    @NotNull
    @Builder.Default
    @Column(name = "date_added", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateAdded = Instant.now();

    @Column(name = "date_modified", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateModified;

    public String getFullName() {
        if (StringUtils.isBlank(middleName) && StringUtils.isNotBlank(lastName)) {
            return (lastName + " " + firstName).trim();
        } else if (StringUtils.isBlank(middleName) && StringUtils.isBlank(lastName)) {
            return firstName.trim();
        } else {
            return (lastName + " " + firstName + " " + middleName).trim();
        }
    }

    public String getShortName() {
        final String firstInitial = firstName.isEmpty() ? "" : firstName.charAt(0) + ".";
        return (lastName + " " + firstInitial).trim();
    }

    public Boolean getDeactivated() {
        return DefaultConstants.DELETED.equals(recStatus);
    }
}