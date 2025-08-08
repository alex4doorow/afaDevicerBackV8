package com.afa.devicer.back.entities.people;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IEmployee extends
        JpaRepository<Employee, UUID>,
        JpaSpecificationExecutor<Employee> {

    @Query("""
    select e from Employee e where (e.person.recStatus = 'A')
    """)
    List<Employee> getAllEmployees();

    Optional<Employee> findByPersonId(Long id);
}
