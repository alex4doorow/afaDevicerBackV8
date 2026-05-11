package com.afa.devicer.back.mappers;

import com.afa.core.dto.orders.OrderDto;
import com.afa.core.dto.orders.OrderItemDto;
import com.afa.core.dto.orders.OrderSaveRequest;
import com.afa.core.dto.orders.OrderStatusHistoryDto;
import com.afa.core.dto.people.PersonShortDto;
import com.afa.core.enums.AmountTypes;
import com.afa.devicer.back.entities.orders.Order;
import com.afa.devicer.back.entities.orders.OrderItem;
import com.afa.devicer.back.entities.orders.OrderStatusHistory;
import com.afa.devicer.back.services.ProductService;
import com.afa.devicer.back.utils.PersonHelper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@SuppressWarnings({"PMD.LawOfDemeter"})
public abstract class OrderMapper {

    @Autowired
    protected ProductService productService;

    @Mapping(target = "userAdded", expression = "java(getUserAdded(entity))")
    @Mapping(target = "amounts", expression = "java(getAmounts(entity))")
    @Mapping(target = "statusHistory", expression = "java(getStatusHistory(entity))")
    @Mapping(target = "items", expression = "java(getItems(entity))")
    public abstract OrderDto fromOrder(Order entity);

    public abstract OrderStatusHistoryDto fromStatusHistory(OrderStatusHistory statusHistory);

    public abstract OrderItemDto fromOrderItem(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateAdded", ignore = true)
    @Mapping(target = "dateModified", ignore = true)
    @Mapping(target = "userAdded", ignore = true)
    @Mapping(target = "statusHistory", ignore = true)
    @Mapping(target = "items", expression = "java(getItemSaveRequestList(request))")
    public abstract Order fromRequest(OrderSaveRequest request);

    public List<OrderItemDto> getItems(final Order entity) {
        if (entity == null || entity.getItems() == null) {
            return Collections.emptyList();
        }
        return entity.getItems().stream()
                .sorted(Comparator.comparing(OrderItem::getItemNum))
                .map(this::fromOrderItem)
                .toList();
    }

    public List<OrderDto> fromOrders(final List<Order> orders) {
        return orders.stream()
                .map(this::fromOrder)
                .toList();
    }

    protected List<OrderStatusHistoryDto> getStatusHistory(final Order entity) {
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

    protected PersonShortDto getUserAdded(final Order entity) {
        return PersonHelper.fromPerson(entity.getUserAdded());
    }

    protected Map<AmountTypes, BigDecimal> getAmounts(final Order entity) {
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

    protected Set<OrderItem> getItemSaveRequestList(final OrderSaveRequest request) {
        if (request == null || request.getItems() == null) {
            return Collections.emptySet();
        }
        return request.getItems().stream()
                .map(oisr -> {
                    final OrderItem orderItem = new OrderItem();
                    orderItem.setItemNum(oisr.getItemNum());
                    if (oisr.getProductId() != null) {
                        orderItem.setProduct(productService.findByIdOrThrow(oisr.getProductId()));
                    }
                    orderItem.setPrice(oisr.getPrice());
                    orderItem.setQuantity(oisr.getQuantity());
                    orderItem.setDiscountRate(oisr.getDiscountRate());
                    orderItem.setAmount(oisr.getAmount());
                    return orderItem;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}