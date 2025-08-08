package com.afa.devicer.back.entities.companies;

import com.afa.devicer.back.entities.dictionaries.Country;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "bp_companies", indexes = {
        @Index(name = "idx_bp_companies_inn", columnList = "inn"),
        @Index(name = "idx_bp_companies_short_name", columnList = "short_name"),
})
@SuppressWarnings({"PMD.TooManyFields", "PMD.AvoidDuplicateLiterals", "PMD.LawOfDemeter"})
public class Company {

    @Id
    @NotNull
    @SequenceGenerator(name = "d_sequence", sequenceName = "d_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Country country;

    @Column(name = "inn", length = 12)
    private String inn;

    @NotNull
    @Column(name = "short_name", nullable = false)
    private String shortName;

    @Column(name = "long_name")
    private String longName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @NotNull
    @Column(name = "rec_status", nullable = false)
    private Character recStatus;

    @NotNull
    @Column(name = "date_added", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateAdded = Instant.now();

    @Column(name = "date_modified", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateModified;
}
