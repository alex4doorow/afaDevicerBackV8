package com.afa.devicer.back.entities.products;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IStockSupplierProduct extends JpaRepository<StockSupplierProduct, Long> {

    List<StockSupplierProduct> findByProductId(Long productId);

}
