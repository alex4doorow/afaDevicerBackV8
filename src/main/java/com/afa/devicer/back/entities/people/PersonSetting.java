package com.afa.devicer.back.entities.people;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "bp_person_settings",
        indexes = {
                @Index(name = "uq_bp_person_settings_person_id", columnList = "person_id", unique = true)
        })
public class PersonSetting {

    @Id
    @NotNull
    @SequenceGenerator(name = "d_sequence", sequenceName = "d_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Person person;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> data;
}
