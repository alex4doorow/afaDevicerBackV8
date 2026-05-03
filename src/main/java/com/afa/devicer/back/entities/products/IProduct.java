package com.afa.devicer.back.entities.products;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IProduct extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

}
