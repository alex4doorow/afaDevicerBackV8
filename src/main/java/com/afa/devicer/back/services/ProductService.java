package com.afa.devicer.back.services;

import com.afa.devicer.back.entities.products.IProduct;
import com.afa.devicer.back.entities.products.Product;
import com.afa.devicer.back.entities.products.Product_;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final IProduct iProduct;

    public Optional<Product> findByIdOptional(final Long id) {
        return iProduct.findById(id);
    }

    public Product findByIdOrThrow(final Long id) {
        return findByIdOptional(id).orElseThrow(() ->
                new DevicerException(DevicerErrors.DB_ENTITY_NOT_FOUND, Product_.class_, id)
        );
    }

}
