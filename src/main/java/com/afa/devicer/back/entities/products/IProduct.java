package com.afa.devicer.back.entities.products;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface IProduct extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("""
                select count(pm) > 0
                from ProductMarketplace pm
                where pm.product.id = :productId
                  and pm.marketSeller = true
            """)
    boolean existsMarketSellerByProductId(Long productId);

}
