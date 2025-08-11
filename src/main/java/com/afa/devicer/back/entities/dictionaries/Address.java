package com.afa.devicer.back.entities.dictionaries;

import com.afa.devicer.back.entities.people.Person;
import com.afa.devicer.back.enums.AddressTypes;
import com.afa.devicer.back.utils.DefaultConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_addresses")
@SuppressWarnings({"PMD.TooManyFields", "PMD.AvoidDuplicateLiterals", "PMD.LawOfDemeter"})
public class Address {

    @Id
    @NotNull
    @SequenceGenerator(name = "d_sequence", sequenceName = "d_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @Column(name = "address_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AddressTypes type;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Country country;

    @Column(name = "post_code", length = 6)
    private String postCode;

    @Column(name = "street")
    private String street;

    @Column(name = "house",  length = 128)
    private String house;

    @Column(name = "flat",  length = 128)
    private String flat;

    @NotNull
    @Column(name = "address_line", nullable = false)
    private String addressLine;

    @NotNull
    @Builder.Default
    @Column(name = "rec_status", nullable = false)
    private Character recStatus = DefaultConstants.ACTIVE;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_added", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Person userAdded;

    @NotNull
    @Builder.Default
    @Column(name = "date_added", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateAdded = Instant.now();

    @Column(name = "date_modified", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateModified;
}
