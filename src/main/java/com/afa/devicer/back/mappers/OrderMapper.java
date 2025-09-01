package com.afa.devicer.back.mappers;

import com.afa.core.dto.orders.OrderDto;
import com.afa.core.dto.persons.PersonShortDto;
import com.afa.core.enums.AmountTypes;
import com.afa.devicer.back.entities.orders.Order;
import com.afa.devicer.back.utils.persons.PersonHelper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@SuppressWarnings({"PMD.LawOfDemeter"})
public interface OrderMapper {

    @Mapping(target = "userAdded", expression = "java(getUserAdded(order))")
    @Mapping(target = "amounts", expression = "java(getAmounts(order))")
//    @Mapping(target = "delivery.deliveryType", expression = "java(getDeliveryType(order.delivery.deliveryType))")
    OrderDto fromOrder(Order order);

    default List<OrderDto> fromOrders(
            final List<Order> orders) {
        return orders.stream()
                .map(this::fromOrder)
                .toList();
    }

    default PersonShortDto getUserAdded(final Order order) {
        return PersonHelper.fromPerson(order.getUserAdded());
    }

    default Map<AmountTypes, BigDecimal> getAmounts(final Order order) {
        final Map<AmountTypes, BigDecimal> amounts = new HashMap<>();
        amounts.put(AmountTypes.TOTAL_WITH_DELIVERY, new BigDecimal("0.00"));
        amounts.put(AmountTypes.TOTAL, order.getTotalAmount());
        amounts.put(AmountTypes.BILL, order.getBillAmount());
        amounts.put(AmountTypes.SUPPLIER, order.getSupplierAmount());
        amounts.put(AmountTypes.MARGIN, order.getMarginAmount());
        amounts.put(AmountTypes.DELIVERY, order.getDelivery().getPrice());
        amounts.put(AmountTypes.POSTPAY, order.getPostpayAmount());

        return amounts;
    }

//    @Mapping(target = "code", expression = "java(deliveryType.name())")
//    @Mapping(target = "annotation", source = "annotation")
//    DeliveryTypesDto getDeliveryType(DeliveryTypes deliveryType);

}