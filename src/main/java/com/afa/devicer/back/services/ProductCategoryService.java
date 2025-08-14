package com.afa.devicer.back.services;

import com.afa.devicer.back.entities.products.IProductCategory;
import com.afa.devicer.back.entities.products.ProductCategory;
import com.afa.devicer.back.entities.products.ProductCategory_;
import com.afa.devicer.back.enums.DevicerErrors;
import com.afa.devicer.back.exceptions.DevicerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final IProductCategory iProductCategory;

    public Optional<ProductCategory> findByIdOptional(final Long id) {
        return iProductCategory.findById(id);
    }

    public ProductCategory findByIdOrThrow(final Long id) {
        return findByIdOptional(id).orElseThrow(() ->
                new DevicerException(DevicerErrors.DB_ENTITY_NOT_FOUND, ProductCategory_.class_, id)
        );
    }

}
