package com.afa.devicer.back.mappers;

import com.afa.devicer.back.dto.orders.OrderDto;
import com.afa.devicer.back.entities.orders.Order;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@SuppressWarnings({"PMD.LawOfDemeter"})
public interface OrderMapper {
    OrderDto fromOrder(Order order);

    default List<OrderDto> fromOrders(
            final List<Order> orders) {
        return orders.stream()
                .map(this::fromOrder)
                .toList();
    }
}