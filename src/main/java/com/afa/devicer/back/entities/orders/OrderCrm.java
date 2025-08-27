package com.afa.devicer.back.entities.orders;

import com.afa.devicer.back.entities.people.Person;
import com.afa.core.enums.CrmStatuses;
import com.afa.core.enums.CrmTypes;
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
@Table(name = "bp_order_crms",
        indexes = {
                @Index(name = "idx_bp_order_crms_order_id", columnList = "order_id"),
                @Index(name = "idx_bp_order_crms_parent_crm_id", columnList = "parent_crm_id"),
                @Index(name = "idx_bp_order_crms_parent_crm_code", columnList = "parent_crm_code")
        })
public class OrderCrm {

    @Id
    @NotNull
    @SequenceGenerator(name = "d_sequence", sequenceName = "d_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Order order;

    @NotNull
    @Column(name = "crm", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private CrmTypes crm;

    @NotNull
    @Column(name = "crm_status", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private CrmStatuses crmStatus;

    @Column(name = "parent_crm_id", nullable = false, length = 128)
    private String parentCrmId;

    @Column(name = "parent_crm_code", nullable = false, length = 128)
    private String parentCrmCode;

    @NotNull
    @Column(name = "rec_status", nullable = false)
    private Character recStatus;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_added", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Person userAdded;

    @NotNull
    @Column(name = "date_added", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateAdded = Instant.now();

    @Column(name = "date_modified", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateModified;
}
