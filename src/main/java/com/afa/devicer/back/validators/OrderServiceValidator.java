package com.afa.devicer.back.validators;

import com.afa.devicer.back.dto.orders.OrderSaveRequest;
import com.afa.devicer.back.services.CustomerService;
import com.afa.devicer.back.services.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.CouplingBetweenObjects", "PMD.ExcessiveImports", "PMD.TooManyMethods",
        "PMD.GodClass", "PMD.CyclomaticComplexity"})
public class OrderServiceValidator {

    private final CustomerService customerService;
    private final ProductCategoryService productCategoryService;

    public void validateOrderCreating(final OrderSaveRequest request) {
        customerService.findByIdOrThrow(request.getCustomerId());
        productCategoryService.findByIdOrThrow(request.getProductCategoryId());
    }

}