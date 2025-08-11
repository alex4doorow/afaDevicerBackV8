package com.afa.devicer.back.entities.orders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IOrder extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {


//    List<Vacancy> findAllByProject(VacancyProject project);
//    Optional<Vacancy> findFirstByProjectIdOrderByInternalIdDesc(UUID projectId);
}
