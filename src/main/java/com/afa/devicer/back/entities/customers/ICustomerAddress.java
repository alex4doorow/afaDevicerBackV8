package com.afa.devicer.back.entities.customers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ICustomerAddress extends JpaRepository<CustomerAddress, Long>, JpaSpecificationExecutor<CustomerAddress> {

}

