package com.afa.devicer.back.mappers;

import com.afa.core.dto.products.ProductShortDto;
import com.afa.devicer.back.entities.products.Product;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@SuppressWarnings({"PMD.LawOfDemeter"})
public interface ProductMapper {

    ProductShortDto fromProduct(Product entity);


}
