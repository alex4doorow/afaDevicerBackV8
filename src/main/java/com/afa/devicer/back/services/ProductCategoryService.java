package com.afa.devicer.back.services;

import com.afa.core.dto.products.ProductCategoryDto;
import com.afa.core.dto.products.ProductCategoryFilter;
import com.afa.devicer.back.config.CacheConfig;
import com.afa.devicer.back.entities.products.IProductCategory;
import com.afa.devicer.back.entities.products.ProductCategory;
import com.afa.devicer.back.entities.products.ProductCategory_;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import com.afa.devicer.back.mappers.dictionaries.ProductCategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final IProductCategory iProductCategory;
    private final ProductCategoryMapper mapper;

    public Optional<ProductCategory> findByIdOptional(final Long id) {
        return iProductCategory.findById(id);
    }

    public ProductCategory findByIdOrThrow(final Long id) {
        return findByIdOptional(id).orElseThrow(() ->
                new DevicerException(DevicerErrors.DB_ENTITY_NOT_FOUND, ProductCategory_.class_, id)
        );
    }

    @Cacheable(cacheNames = CacheConfig.CACHE_PRODUCT_CATEGORY)
    @Transactional(readOnly = true)
    public List<ProductCategoryDto> getFiltered(final ProductCategoryFilter filter) {
        return iProductCategory.findAllActive().stream()
                .map(mapper::fromProductCategory)
                .toList();
    }

}
