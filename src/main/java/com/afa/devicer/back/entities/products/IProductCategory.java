package com.afa.devicer.back.entities.products;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IProductCategory extends JpaRepository<ProductCategory, Long> {

    @Query("""
            select pc from ProductCategory pc
                        where pc.recStatus = 'A'
                        order by pc.id
            """)
    List<ProductCategory> findAllActive();


}
