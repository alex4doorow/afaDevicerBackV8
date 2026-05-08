package com.afa.devicer.back.controllers.integrations;

import com.afa.core.dto.customers.CustomerConditionsDto;
import com.afa.core.dto.customers.CustomerSearchPagedFilter;
import com.afa.core.dto.customers.CustomerSingleResponse;
import com.afa.core.dto.integrations.union.CustomerDataUnionResponse;
import com.afa.core.dto.orders.OrderConditionsDto;
import com.afa.core.dto.orders.OrderDto;
import com.afa.core.dto.orders.OrderPagedFilter;
import com.afa.devicer.back.services.CustomerService;
import com.afa.devicer.back.services.OrderService;
import com.afa.devicer.back.services.UserInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static com.afa.devicer.back.controllers.internal.ControllerConstants.INTEGRATIONS_UNION;

@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(INTEGRATIONS_UNION)
public class UnionController {

    private final UserInfoService userInfoService;
    private final CustomerService customerService;
    private final OrderService orderService;

    @PostMapping("/customers/suggest")
    public ResponseEntity<CustomerDataUnionResponse> getCustomerSuggest(
            @AuthenticationPrincipal final Jwt principal,
            @Valid @RequestBody final CustomerSearchPagedFilter filter) {

        final CustomerSingleResponse customerResponse = customerService.getSingleSearchFiltered(filter);
        if (customerResponse.getCustomer() == null) {
            return ResponseEntity.ok(new CustomerDataUnionResponse(null, Collections.emptyList()));
        }
        final OrderPagedFilter orderFilter = OrderPagedFilter.builder()
                .conditions(OrderConditionsDto.builder()
                        .periodExist(false)
                        .customerConditions(CustomerConditionsDto.builder()
                                .id(customerResponse.getCustomer().getId())
                                .build())
                        .build())
                .build();
        final List<OrderDto> orders = orderService.getFiltered(userInfoService.fillUserInfo(principal),
                orderFilter).getOrders();
        return ResponseEntity.ok(
                new CustomerDataUnionResponse(customerResponse.getCustomer(), orders)
        );
    }
}
