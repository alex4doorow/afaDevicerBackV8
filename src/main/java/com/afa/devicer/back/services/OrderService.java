package com.afa.devicer.back.services;

import com.afa.devicer.back.dto.UserInfoDto;
import com.afa.devicer.back.dto.orders.OrderDto;
import com.afa.devicer.back.dto.orders.OrderPagedFilter;
import com.afa.devicer.back.dto.orders.OrderPagedResponse;
import com.afa.devicer.back.entities.orders.IOrder;
import com.afa.devicer.back.entities.orders.Order;
import com.afa.devicer.back.entities.orders.Order_;
import com.afa.devicer.back.mappers.OrderMapper;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.CouplingBetweenObjects", "PMD.ExcessiveImports", "PMD.TooManyMethods",
        "PMD.GodClass", "PMD.CyclomaticComplexity"})
public class OrderService {
    
    private final IOrder iOrder;

    private final OrderMapper mapper;

    @Transactional
    @SuppressWarnings("PMD.UnnecessaryLocalBeforeReturn")
    public OrderPagedResponse getFiltered(
            final UserInfoDto user,
            final OrderPagedFilter filter) {

        final List<Order> orders = iOrder.findAll();
        log.debug(orders.toString());

        final Page<Order> page = iOrder.findAll(fillOrderSpecification(user, filter),
                filter.createPageRequest(filter.isSortedByEmpty() ? "id desc" : filter.getSortedBy(),
                        Order_.class.getDeclaredFields()));

        final List<OrderDto> orderDtos = mapper.fromOrders(page.getContent());

        return new OrderPagedResponse(
                page.getTotalElements(), page.getTotalPages(),
                page.hasPrevious(), page.hasNext(),
                orderDtos);

    }

    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.NPathComplexity", "PMD.UnusedFormalParameter"})
    private Specification<Order> fillOrderSpecification(final UserInfoDto user, final OrderPagedFilter filter) {

        return (root, query, builder) -> {

            final List<Predicate> predicates = new ArrayList<>();

            // order id
            if (filter.getOrderId() != null) {
                predicates.add(builder.equal(root.get(Order_.ID), filter.getOrderId()));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
