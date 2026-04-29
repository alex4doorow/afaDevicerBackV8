package com.afa.devicer.back.mappers;

import com.afa.core.dto.orders.OrderDto;
import com.afa.core.dto.orders.OrderItemDto;
import com.afa.core.dto.orders.OrderPresentationStatusDto;
import com.afa.core.dto.orders.OrderStatusHistoryDto;
import com.afa.core.dto.persons.PersonShortDto;
import com.afa.core.enums.AmountTypes;
import com.afa.devicer.back.entities.orders.Order;
import com.afa.devicer.back.entities.orders.OrderItem;
import com.afa.devicer.back.entities.orders.OrderStatusHistory;
import com.afa.devicer.back.utils.persons.PersonHelper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@SuppressWarnings({"PMD.LawOfDemeter"})
public interface OrderMapper {

    @Mapping(target = "userAdded", expression = "java(getUserAdded(entity))")
    @Mapping(target = "amounts", expression = "java(getAmounts(entity))")
    @Mapping(target = "statusHistory", expression = "java(getStatusHistory(entity))")
    @Mapping(target = "items", expression = "java(getItems(entity))")
    OrderDto fromOrder(Order entity);

    OrderStatusHistoryDto fromStatusHistory(OrderStatusHistory statusHistory);

    OrderItemDto fromOrderItem(OrderItem orderItem);

    default List<OrderStatusHistoryDto> getStatusHistory(final Order entity) {
        if (entity == null || entity.getStatusHistory() == null) {
            return Collections.emptyList();
        }

        final AtomicInteger counter = new AtomicInteger(1);
        return entity.getStatusHistory().stream()
                .sorted(Comparator.comparing(OrderStatusHistory::getDateAdded,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::fromStatusHistory)
                .peek(dto -> dto.setNum(counter.getAndIncrement()))
                .toList();
    }

    default List<OrderItemDto> getItems(final Order entity) {
        if (entity == null || entity.getItems() == null) {
            return Collections.emptyList();
        }
        return entity.getItems().stream()
                .sorted(Comparator.comparing(OrderItem::getItemNum))
                .map(this::fromOrderItem)
                .toList();
    }

    default List<OrderDto> fromOrders(
            final List<Order> orders) {
        return orders.stream()
                .map(this::fromOrder)
                .peek(dto -> dto.setPresentation(OrderPresentationStatusDto.createOrderPresentationStatusDto(dto)))
                .toList();
    }

    default PersonShortDto getUserAdded(final Order entity) {
        return PersonHelper.fromPerson(entity.getUserAdded());
    }

    default Map<AmountTypes, BigDecimal> getAmounts(final Order entity) {
        final Map<AmountTypes, BigDecimal> amounts = new HashMap<>();
        amounts.put(AmountTypes.TOTAL_WITH_DELIVERY, entity.getTotalWithDeliveryAmount());
        amounts.put(AmountTypes.TOTAL, entity.getTotalAmount());
        amounts.put(AmountTypes.BILL, entity.getBillAmount());
        amounts.put(AmountTypes.SUPPLIER, entity.getSupplierAmount());
        amounts.put(AmountTypes.MARGIN, entity.getMarginAmount());
        amounts.put(AmountTypes.DELIVERY, entity.getDelivery().getPrice());
        amounts.put(AmountTypes.POSTPAY, entity.getPostpayAmount());

        return amounts;
    }

    default OrderPresentationStatusDto getPresentationStatus(final OrderDto orderDto) {
        return OrderPresentationStatusDto.createOrderPresentationStatusDto(orderDto);
    }
}