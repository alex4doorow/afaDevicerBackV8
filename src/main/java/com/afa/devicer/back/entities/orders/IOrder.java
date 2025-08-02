package com.afa.devicer.back.entities.orders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface IOrder extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

//    List<Vacancy> findAllByProject(VacancyProject project);
//    Optional<Vacancy> findFirstByProjectIdOrderByInternalIdDesc(UUID projectId);
}
