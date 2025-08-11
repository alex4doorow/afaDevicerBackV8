package com.afa.devicer.back.entities.companies;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICompany extends JpaRepository<Company, Long> {

    Optional<Company> findAllByInn(String inn);

}