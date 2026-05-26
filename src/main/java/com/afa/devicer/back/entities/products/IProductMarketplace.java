package com.afa.devicer.back.entities.products;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IProductMarketplace extends JpaRepository<ProductMarketplace, Long>, JpaSpecificationExecutor<ProductMarketplace> {

}
