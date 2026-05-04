package com.afa.devicer.back.entities.customers;

import com.afa.core.enums.ContactTypes;
import com.afa.devicer.back.entities.companies.Company;
import com.afa.devicer.back.entities.people.Person;
import com.afa.core.enums.CustomerTypes;
import com.afa.core.utils.DefaultConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_customers")
public class Customer {

    @Id
    @NotNull
    @SequenceGenerator(name = "d_sequence", sequenceName = "d_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @Column(name = "customer_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomerTypes type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @ToString.Exclude
    @JsonIgnore
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    @ToString.Exclude
    @JsonIgnore
    private Person person;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private Set<CustomerAddress> addresses;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private Set<CustomerContact> contacts;

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

    public boolean isPerson() {
        return person != null;
    }

    public boolean isCompany() {
        return company != null;
    }

    public String getViewShortName() {
        return isCompany() ? company.getShortName() : person.getShortName();
    }

    public Person getMainContact() {
        if (isPerson()) {
            return person;
        }

        if (!isCompany() || getContacts() == null) {
            return null;
        }

        return getContacts().stream()
                .filter(contact -> contact.getType() == ContactTypes.MAIN)
                .map(CustomerContact::getPerson)
                .findFirst()
                .orElse(null);
    }
}
