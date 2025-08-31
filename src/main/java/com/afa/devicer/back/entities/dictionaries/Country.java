package com.afa.devicer.back.entities.dictionaries;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "d_countries",
        indexes = {
                @Index(name = "idx_countries_iso_code_2", columnList = "iso_code_2", unique = true),
                @Index(name = "idx_countries_iso_code_3", columnList = "iso_code_3", unique = true),
                @Index(name = "idx_countries_isd_code", columnList = "isd_code")
        })
public class Country {

    @Id
    private UUID id = UUID.randomUUID();

    @NotNull
    @Column(name = "iso_code_2", length = 3, nullable = false)
    private String isoCode2;

    @NotNull
    @Column(name = "iso_code_3", length = 3, nullable = false)
    private String isoCode3;

    @NotNull
    @Column(name = "isd_code", length = 32, nullable = false)
    private String isdCode;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "rec_status", nullable = false)
    private Character recStatus;

    @NotNull
    @Column(name = "date_added", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateAdded = Instant.now();

    @Column(name = "date_modified", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateModified;
}
