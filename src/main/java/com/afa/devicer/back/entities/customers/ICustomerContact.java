package com.afa.devicer.back.entities.customers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ICustomerContact extends JpaRepository<CustomerContact, Long>, JpaSpecificationExecutor<CustomerContact> {

}

