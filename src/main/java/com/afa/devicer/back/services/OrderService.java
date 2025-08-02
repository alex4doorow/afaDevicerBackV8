package com.afa.devicer.back.services;

import com.afa.devicer.back.dto.orders.OrderDto;
import com.afa.devicer.back.dto.orders.OrderPagedFilter;
import com.afa.devicer.back.dto.orders.OrderPagedResponse;
import com.afa.devicer.back.entities.orders.IOrder;
import com.afa.devicer.back.entities.orders.Order;
import com.afa.devicer.back.mappers.OrderMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
            //final UserInfoDto user,
            final OrderPagedFilter filter) {

        final List<Order> orders = iOrder.findAll();
        log.debug(orders.toString());

        final List<OrderDto> orderDtos = mapper.fromOrders(orders);
        /*
        final Page<Order> page = iVacancy.findAll(fillVacancySpecification(user, filter),
                filter.createPageRequest(filter.isSortedByEmpty() ? "tsCreated desc" : filter.getSortedBy(),
                        Vacancy_.class.getDeclaredFields()));

        final List<OrderDto> vacancies = mapper.fromVacancies(page.getContent(),
                vacancy -> clearSecretData(vacancy, user));

        return new OrderPagedResponse(
                page.getTotalElements(), page.getTotalPages(),
                page.hasPrevious(), page.hasNext(),
                vacancies);

         */

/*
        return new OrderPagedResponse(
                100, 1,
                false, false,
                List.of(OrderDto.builder()
                        .id(101L)
                        .orderNum(101L)
                        .build(),
                        OrderDto.builder()
                                .id(102L)
                                .orderNum(102L)
                                .build()));
*/


        return new OrderPagedResponse(
                100, 1,
                false, false,
                orderDtos);


    }
}
