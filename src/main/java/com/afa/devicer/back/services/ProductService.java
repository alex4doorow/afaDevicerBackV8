package com.afa.devicer.back.services;

import com.afa.core.dto.products.Result4UpdateProductStock;
import com.afa.core.enums.CrmTypes;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.enums.OrderStatusTypes;
import com.afa.core.exceptions.DevicerException;
import com.afa.devicer.back.entities.products.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final IProduct iProduct;
    private final IStockSupplierProduct iStockSupplierProduct;

    public Optional<Product> findByIdOptional(final Long id) {
        return iProduct.findById(id);
    }

    public Product findByIdOrThrow(final Long id) {
        return findByIdOptional(id).orElseThrow(() ->
                new DevicerException(DevicerErrors.DB_ENTITY_NOT_FOUND, Product_.class_, id)
        );
    }

    /**
     * Обновляет остатки продуктов (на витрине и на нашем складе) после прохода заказа через фазы (заявка - подтверждено)
     *
     * @param product
     * @param deltaQuantity
     * @param crmType       [CrmTypes.YANDEX_MARKET, CrmTypes.OZON, обычный лид]
     * @param phase         [заявка маркета, OrderStatuses.BID, OrderStatuses.APPROVED]
     */
    public void updateDeltaQuantityProduct(final Product product,
                                           final int deltaQuantity,
                                           final CrmTypes crmType,
                                           final OrderStatusTypes phase) {

        final Result4UpdateProductStock result4UpdateProductStock = checkUpdateProductStockByPhase(product, crmType, phase);
        updateDeltaQuantityProduct(product, deltaQuantity, result4UpdateProductStock);
    }

    public void updateDbProductQuantityByDelta(final Long productId, final int deltaQuantity) {
        final Product product = findByIdOrThrow(productId);
        product.setQuantity(product.getQuantity() - deltaQuantity);
        iProduct.saveAndFlush(product);
    }

    public void updateDbProductStock(final StockSupplierProduct supplierStockProduct,
                                     final int deltaQuantity) {

        final int newQuantity = supplierStockProduct.getQuantity() - deltaQuantity;
        supplierStockProduct.setQuantity(newQuantity);
        iStockSupplierProduct.save(supplierStockProduct);
        iStockSupplierProduct.flush();
    }

    /**
     * Обновление остатков товаров на витрине и беке по флагам из result4UpdateProductStock
     *
     * @param product
     * @param deltaQuantity
     * @param result4UpdateProductStock
     */
    @SuppressWarnings("PMD")
    private void updateDeltaQuantityProduct(final Product product,
                                            final int deltaQuantity,
                                            final Result4UpdateProductStock result4UpdateProductStock) {

        final List<StockSupplierProduct> list = iStockSupplierProduct.findByProductId(product.getId());


        if (!list.isEmpty()) {
            final StockSupplierProduct supplierStockProduct = list.getFirst();

            if (result4UpdateProductStock.isProductFront()) {
                int newWikiQuantity = supplierStockProduct.getProduct().getQuantity();
                newWikiQuantity = newWikiQuantity - deltaQuantity;

                updateDbProductQuantityByDelta(product.getId(), deltaQuantity);
                product.setQuantity(newWikiQuantity);
            }
            if (result4UpdateProductStock.isProductBack()) {
                updateDbProductStock(supplierStockProduct, supplierStockProduct.getQuantity() - deltaQuantity);
            }

            if (supplierStockProduct.getProduct().getComposite()) {
                // это комплект - списываем компоненты slaves
                for (final ProductComposite kitComponent : supplierStockProduct.getProduct().getKitComponents()) {
                    final Product slave = kitComponent.getSlave();

                    if (result4UpdateProductStock.isSlaveBack()) {
                        final List<StockSupplierProduct> list2 = iStockSupplierProduct.findByProductId(slave.getId());
                        final StockSupplierProduct stockSupplierSlaveProduct = list2.getFirst();

                        int slaveQuantity = stockSupplierSlaveProduct.getSupplierQuantity();
                        final int deltaSlaveQuantity = stockSupplierSlaveProduct.getSupplierQuantity() * deltaQuantity;

                        slaveQuantity = slaveQuantity - deltaSlaveQuantity;

                        updateDbProductStock(stockSupplierSlaveProduct, deltaSlaveQuantity);


                    }
                    if (result4UpdateProductStock.isSlaveFront()) {
                        /*
                        int slaveQuantity = slave.getQuantity();
                        int deltaSlaveQuantity = slave.getSlaveQuantity() * deltaQuantity;

                        slaveQuantity = slaveQuantity - deltaSlaveQuantity;

                        updateDbProductQuantityByDelta(slave.getId(), deltaSlaveQuantity);
                        Product wikiProduct = getProductById(slave.getId());
                        wikiProduct.setQuantity(slaveQuantity);

                        */
                    }
                }
                // перебрать все комплекты (где встречаются наши слейвы) и актуализировать на фронте - после списания всех слейвов
                if (result4UpdateProductStock.isSlaveFront()) {
                    //recalculateCompositesAfterSlavesExecute(supplierStockProduct.getProduct());
                }
            }
        }
    }

    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.CyclomaticComplexity"})
    private Result4UpdateProductStock checkUpdateProductStockByPhase(final Product product,
                                                                     final CrmTypes crmType,
                                                                     final OrderStatusTypes phase) {


        final Result4UpdateProductStock result4UpdateProductStock = new Result4UpdateProductStock();
        final List<StockSupplierProduct> list = iStockSupplierProduct.findByProductId(product.getId());
        if (list.isEmpty()) {
            return null;
        }
        final StockSupplierProduct stockSupplierProduct = iStockSupplierProduct.findByProductId(product.getId()).getFirst();

        if (stockSupplierProduct.getProduct().getComposite()) {

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

}
