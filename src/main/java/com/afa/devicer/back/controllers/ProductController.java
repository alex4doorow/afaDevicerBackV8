package com.afa.devicer.back.controllers;

import com.afa.core.dto.products.ProductCategoryFilter;
import com.afa.core.dto.products.ProductCategoryResponse;
import com.afa.core.dto.products.ProductFilter;
import com.afa.core.dto.products.ProductResponse;
import com.afa.devicer.back.services.ProductCategoryService;
import com.afa.devicer.back.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import static com.afa.devicer.back.controllers.internal.ControllerConstants.PRODUCTS;
import static com.afa.devicer.back.controllers.internal.ControllerConstants.ROLE_ADMIN;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(PRODUCTS)
@Tag(name = "Products controller", description = "Operations pertaining to...")
@Secured({ROLE_ADMIN})
public class ProductController {

    private final ProductCategoryService productCategoryService;
    private final ProductService productService;

    @GetMapping("/productCategories")
    @Operation(summary = "Find all product categories")
    public ResponseEntity<ProductCategoryResponse> getProductCategoryFiltered(
            @Valid @ModelAttribute final ProductCategoryFilter filter
    ) {
        return ResponseEntity.ok(
                new ProductCategoryResponse(productCategoryService.getFiltered(filter))
        );
    }

    @GetMapping("/suggest")
    @Operation(summary = "Find all products")
    public ResponseEntity<ProductResponse> getProductsSuggest(
            @Valid @ModelAttribute final ProductFilter filter
    ) {
        return ResponseEntity.ok(
                new ProductResponse(productService.getProductsSuggest(filter))
        );
    }
}
