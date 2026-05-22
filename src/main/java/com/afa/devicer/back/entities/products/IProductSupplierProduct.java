package com.afa.devicer.back.entities.products;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IProductSupplierProduct extends JpaRepository<ProductSupplierPrice, Long> {

    List<ProductSupplierPrice> findByProductId(Long productId);

}
