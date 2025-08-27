package com.afa.devicer.back.mappers;

import com.afa.core.dto.orders.OrderDto;
import com.afa.core.dto.persons.PersonShortDto;
import com.afa.devicer.back.entities.orders.Order;
import com.afa.devicer.back.utils.persons.PersonHelper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@SuppressWarnings({"PMD.LawOfDemeter"})
public interface OrderMapper {

    @Mapping(target = "userAdded", expression = "java(getUserAdded(order))")
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
}