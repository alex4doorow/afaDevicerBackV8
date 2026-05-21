package com.afa.devicer.back.mappers.dictionaries;

import com.afa.core.dto.delivery.DeliveryPriceDto;
import com.afa.core.enums.DeliveryPriceTypes;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeliveryPriceTypesMapper {

    DeliveryPriceDto fromDeliveryPriceType(DeliveryPriceTypes entity);
    List<DeliveryPriceDto> fromDeliveryPriceTypes(List<DeliveryPriceTypes> entities);
}
