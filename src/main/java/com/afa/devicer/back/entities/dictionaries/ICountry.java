package com.afa.devicer.back.entities.dictionaries;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ICountry extends JpaRepository<Country, UUID>, JpaSpecificationExecutor<Country> {

    @Query("""
            select c from Country c
                        where c.recStatus = 'A'
                        order by c.id
            """)
    List<Country> findAllActive();

}
