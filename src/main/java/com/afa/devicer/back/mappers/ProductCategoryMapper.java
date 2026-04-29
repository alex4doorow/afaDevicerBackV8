package com.afa.devicer.back.mappers;

import com.afa.core.dto.products.ProductCategoryDto;
import com.afa.devicer.back.entities.products.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@SuppressWarnings({"PMD.LawOfDemeter"})
public interface ProductCategoryMapper {

    ProductCategoryDto fromProductCategory(ProductCategory entity);


}
