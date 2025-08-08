package com.afa.devicer.back.entities.dictionaries;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "d_sys_stores")
public class SysStore {

    @Id
    @NotNull
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @Column(name = "code", nullable = false)
    private String type;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "url", nullable = false)
    private String url;

    @NotNull
    @Column(name = "rec_status", nullable = false)
    private Character recStatus;
}
