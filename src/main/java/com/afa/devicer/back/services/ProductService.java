package com.afa.devicer.back.services;

import com.afa.core.dto.products.ProductDto;
import com.afa.core.dto.products.ProductFilter;
import com.afa.core.dto.products.Result4UpdateProductStock;
import com.afa.core.enums.CrmTypes;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.enums.OrderStatusTypes;
import com.afa.core.exceptions.DevicerException;
import com.afa.devicer.back.config.CacheConfig;
import com.afa.devicer.back.entities.products.*;
import com.afa.devicer.back.mappers.dictionaries.ProductMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final IProduct iProduct;
    private final IProductSupplierProduct iProductSupplierProduct;
    private final IProductComposite iProductComposite;
    private final ProductMapper productMapper;

    public Optional<Product> findByIdOptional(final Long id) {
        return iProduct.findById(id);
    }

    @Cacheable(cacheNames = CacheConfig.CACHE_PRODUCT, key = "#id")
    public Product findByIdOrThrow(final Long id) {
        return findByIdOptional(id).orElseThrow(() ->
                new DevicerException(DevicerErrors.DB_ENTITY_NOT_FOUND, Product_.class_, id)
        );
    }

    @Cacheable(
            cacheNames = CacheConfig.CACHE_PRODUCT_SUGGEST,
            key = "#filter.nameContext.trim().toLowerCase()",
            condition = "#filter.nameContext != null && #filter.nameContext.trim().length() >= 3",
            sync = true
    )
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsSuggest(final ProductFilter filter) {
        if (filter.getNameContext() == null || filter.getNameContext().trim().length() < 3) {
            return Collections.emptyList();
        }
        List<Product> products;
        final ProductFilter skuFilter = ProductFilter.builder()
                .sku(filter.getNameContext())
                .build();
        products = iProduct.findAll(fillProductSpecification(skuFilter));
        if (products.isEmpty()) {
            final ProductFilter longNameFilter = ProductFilter.builder()
                    .longName(filter.getNameContext())
                    .build();
            products = iProduct.findAll(fillProductSpecification(longNameFilter));
        }
        if (products.isEmpty()) {
            final ProductFilter shortNameFilter = ProductFilter.builder()
                    .shortName(filter.getNameContext())
                    .build();
            products = iProduct.findAll(fillProductSpecification(shortNameFilter));
        }
        if (products.isEmpty()) {
            return Collections.emptyList();
        }
        return products.stream()
                .map(productMapper::fromProduct)
                .sorted(Comparator.comparing(ProductDto::getShortName))
                .limit(10)
                .toList();
    }

    /**
     * Обновляет остатки продуктов (на витрине и на нашем складе) после прохода заказа через фазы (заявка - подтверждено)
     *
     * @param product
     * @param deltaQuantity
     * @param crmType       [CrmTypes.YANDEX_MARKET, CrmTypes.OZON, обычный лид]
     * @param phase         [заявка маркета, OrderStatuses.BID, OrderStatuses.APPROVED]
     */
    @Caching(evict = {
            @CacheEvict(
                    cacheNames = CacheConfig.CACHE_PRODUCT,
                    key = "#product.id"
            ),
            @CacheEvict(
                    cacheNames = CacheConfig.CACHE_PRODUCT_SUGGEST,
                    allEntries = true
            )
    })
    public void updateDeltaQuantityProduct(
            final Product product,
            final int deltaQuantity,
            final CrmTypes crmType,
            final OrderStatusTypes phase) {

        final Result4UpdateProductStock result4UpdateProductStock = checkUpdateProductStockByPhase(product, crmType, phase);
        updateDeltaQuantityProduct(product, deltaQuantity, result4UpdateProductStock);
    }

    /**
     * Обновление остатков товаров на витрине и беке по флагам из result4UpdateProductStock
     *
     * @param product
     * @param deltaQuantity
     * @param result4UpdateProductStock
     */
    private void updateDeltaQuantityProduct(
            final Product product,
            final int deltaQuantity,
            final Result4UpdateProductStock result4UpdateProductStock) {

        if (product.getStock() == null) {
            return;
        }

        if (result4UpdateProductStock.isProductFront()) {
            int newWikiQuantity = product.getQuantity();
            newWikiQuantity = newWikiQuantity - deltaQuantity;

            product.setQuantity(newWikiQuantity);
            iProduct.saveAndFlush(product);
        }
        if (result4UpdateProductStock.isProductBack()) {
            updateDeltaQuantityProductStock(product.getStock(), product.getQuantity() - deltaQuantity);
        }

        if (product.getComposite()) {
            // это комплект - списываем компоненты slaves
            for (final ProductComposite kitComponent : product.getStock().getProduct().getKitComponents()) {
                final Product slave = kitComponent.getSlave();

                if (result4UpdateProductStock.isSlaveBack()) {

                    int slaveQuantity = slave.getStock().getSupplierQuantity();
                    final int deltaSlaveQuantity = slave.getStock().getSupplierQuantity() * deltaQuantity;

                    slaveQuantity = slaveQuantity - deltaSlaveQuantity;
                    kitComponent.setSlaveQuantity(slaveQuantity);
                    iProductComposite.saveAndFlush(kitComponent);
                    updateDeltaQuantityProductStock(slave.getStock(), deltaSlaveQuantity);
                }
                if (result4UpdateProductStock.isSlaveFront()) {

                    int slaveQuantity = slave.getQuantity();
                    final int deltaSlaveQuantity = kitComponent.getSlaveQuantity() * deltaQuantity;

                    slaveQuantity = slaveQuantity - deltaSlaveQuantity;
                    slave.setQuantity(slaveQuantity);
                    kitComponent.setSlaveQuantity(slaveQuantity);
                    iProductComposite.saveAndFlush(kitComponent);
                }
            }
        }
    }

    private void updateDeltaQuantityProductStock(
            final ProductSupplierPrice productSupplierPrice,
            final int deltaQuantity) {

        final int newQuantity = productSupplierPrice.getQuantity() - deltaQuantity;
        productSupplierPrice.setQuantity(newQuantity);
        iProductSupplierProduct.save(productSupplierPrice);
        iProductSupplierProduct.flush();
    }

    private Result4UpdateProductStock checkUpdateProductStockByPhase(
            final Product product,
            final CrmTypes crmType,
            final OrderStatusTypes phase) {

        if (product.getStock() == null) {
            return null;
        }
        final Result4UpdateProductStock result4UpdateProductStock = new Result4UpdateProductStock();
        if (product.getComposite()) {

            if ((crmType == CrmTypes.YANDEX_MARKET || crmType == CrmTypes.OPENCART) && phase == OrderStatusTypes.BID) {
                result4UpdateProductStock.setSlaveFront(true);

            } else if ((crmType == CrmTypes.YANDEX_MARKET || crmType == CrmTypes.OPENCART) && phase == OrderStatusTypes.APPROVED) {
                result4UpdateProductStock.setSlaveBack(true);

            } else if ((crmType.isSimple() || crmType == CrmTypes.OZON) && phase == OrderStatusTypes.APPROVED) {
                result4UpdateProductStock.setProductFront(true);
                result4UpdateProductStock.setSlaveFront(true);
                result4UpdateProductStock.setSlaveBack(true);
            }

        } else {
            if ((crmType == CrmTypes.YANDEX_MARKET || crmType == CrmTypes.OPENCART) && phase == OrderStatusTypes.APPROVED) {
                result4UpdateProductStock.setProductBack(true);

            } else if ((crmType.isSimple() || crmType == CrmTypes.OZON) && phase == OrderStatusTypes.APPROVED) {
                result4UpdateProductStock.setProductFront(true);
                result4UpdateProductStock.setProductBack(true);
            }
        }
        return result4UpdateProductStock;
    }

    private Specification<Product> fillProductSpecification(final ProductFilter filter) {

        return (root, query, builder) -> {

            final List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(filter.getSku())) {
                final String search = "%" + filter.getSku().toLowerCase() + "%";
                predicates.add(builder.like(builder.lower(root.get(Product_.SKU)), search));
            }
            if (StringUtils.isNotBlank(filter.getLongName())) {
                final String search = "%" + filter.getLongName().toLowerCase() + "%";
                predicates.add(builder.like(builder.lower(root.get(Product_.LONG_NAME)), search));
            }
            if (StringUtils.isNotBlank(filter.getShortName())) {
                final String search = "%" + filter.getShortName().toLowerCase() + "%";
                predicates.add(builder.like(builder.lower(root.get(Product_.SHORT_NAME)), search));
            }
            if (StringUtils.isNotBlank(filter.getNameContext())) {

                final String search = "%" + filter.getNameContext().trim().toLowerCase() + "%";
                predicates.add(
                        builder.or(
                                builder.like(builder.lower(root.get(Product_.SKU)), search),
                                builder.like(
                                        builder.lower(root.get(Product_.SHORT_NAME)),
                                        search
                                ),
                                builder.like(
                                        builder.lower(root.get(Product_.LONG_NAME)),
                                        search
                                )
                        )
                );
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
