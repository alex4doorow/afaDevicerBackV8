package com.afa.devicer.back.services;

import com.afa.devicer.back.dto.UserInfoDto;
import com.afa.devicer.back.dto.orders.*;
import com.afa.devicer.back.dto.persons.PersonSaveRequest;
import com.afa.devicer.back.entities.dictionaries.Address;
import com.afa.devicer.back.entities.dictionaries.Country;
import com.afa.devicer.back.entities.orders.*;
import com.afa.devicer.back.entities.people.Person;
import com.afa.devicer.back.entities.products.Product;
import com.afa.devicer.back.enums.AddressTypes;
import com.afa.devicer.back.enums.DevicerErrors;
import com.afa.devicer.back.enums.OrderStatusTypes;
import com.afa.devicer.back.exceptions.DevicerException;
import com.afa.devicer.back.mappers.OrderMapper;
import com.afa.devicer.back.validators.OrderServiceValidator;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.CouplingBetweenObjects", "PMD.ExcessiveImports", "PMD.TooManyMethods",
        "PMD.GodClass", "PMD.CyclomaticComplexity"})
public class OrderService {

    private final IOrder iOrder;

    private final UserInfoService userInfoService;
    private final CustomerService customerService;
    private final ProductCategoryService productCategoryService;
    private final ProductService productService;

    private final PersonService personService;
    private final AddressService addressService;

    private final DictionaryCountryService dictionaryCountryService;

    private final OrderServiceValidator validator;

    private final OrderMapper mapper;

    public Optional<Order> findByIdOptional(final Long id) {
        return iOrder.findById(id);
    }

    public Order findByIdOrThrow(final Long id) {
        return findByIdOptional(id).orElseThrow(() ->
                new DevicerException(DevicerErrors.DB_ENTITY_NOT_FOUND, Order_.class_, id)
        );
    }

    public OrderSingleResponse getOrder(
            @NotNull final UserInfoDto user,
            @NotNull final Long orderId) {

        final Order order = findByIdOrThrow(orderId);
        final OrderDto result = mapper.fromOrder(order);
        return new OrderSingleResponse(result);
    }


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

    @Transactional
    public Order create(final UserInfoDto userInfo, final OrderSaveRequest request) {

        validator.validateOrderCreating(request);

        final Country deliveryCountry = dictionaryCountryService.findByIdOrThrow(request.getDelivery().getAddress().getCountryId());
        final Person originator = userInfoService.findByIdOrThrow(userInfo.getPersonId());
        Person recipient = personService.findByPhoneNumber(request.getDelivery().getRecipient().getPhoneNumber());
        if (recipient == null) {
            final PersonSaveRequest recipientRequest = request.getDelivery().getRecipient();
            recipient = personService.create(Person.builder()
                    .country(deliveryCountry)
                    .firstName(recipientRequest.getFirstName())
                    .middleName(recipientRequest.getMiddleName())
                    .lastName(recipientRequest.getLastName())
                    .phoneNumber(recipientRequest.getPhoneNumber())
                    .email(recipientRequest.getEmail())
                    .build());
        }

        Address deliveryAddress = Address.builder()
                .country(deliveryCountry)
                .type(AddressTypes.MAIN)
                .postCode(request.getDelivery().getAddress().getPostCode())
                .street(request.getDelivery().getAddress().getStreet())
                .house(request.getDelivery().getAddress().getHouse())
                .flat(request.getDelivery().getAddress().getFlat())
                .addressLine(request.getDelivery().getAddress().getAddressLine())
                .userAdded(originator)
                .build();
        deliveryAddress = addressService.create(deliveryAddress);

        final Order order = new Order();
        order.setOrderNum(request.getOrderNum());
        order.setOrderDate(request.getOrderDate());
        order.setCustomer(customerService.findByIdOrThrow(request.getCustomerId()));
        order.setType(request.getType());
        order.setSourceType(request.getSourceType());
        order.setAdvertType(request.getAdvertType());
        order.setPaymentType(request.getPaymentType());
        order.setStore(request.getStore());
        order.setProductCategory(productCategoryService.findByIdOrThrow(request.getProductCategoryId()));
        order.setDelivery(OrderDelivery.builder()
                .order(order)
                .price(request.getDelivery().getPrice())
                .deliveryType(request.getDelivery().getDeliveryType())
                .deliveryPaymentType(request.getDelivery().getDeliveryPaymentType())
                .deliveryPriceType(request.getDelivery().getDeliveryPriceType())
                .address(deliveryAddress)
                .recipient(recipient)
                .annotation(request.getDelivery().getAnnotation())
                .deliveryDate(request.getDelivery().getDeliveryDate())
                .timeIn(request.getDelivery().getTimeIn())
                .timeOut(request.getDelivery().getTimeOut())
                .build());
        order.setStatus(OrderStatusTypes.BID);
        order.setAnnotation(request.getAnnotation());
        order.setUserAdded(originator);

        final Set<OrderItem> items = new HashSet<>();
        if (request.getItems() != null && !request.getItems().isEmpty()) {

            for (final OrderItemSaveRequest ir :  request.getItems()) {
                final Product product = productService.findByIdOrThrow(ir.getProductId());

                  items.add(OrderItem.builder()
                        .itemNum(ir.getItemNum())
                        .order(order)
                        .product(product)
                        .price(ir.getPrice())
                        .quantity(ir.getQuantity())
                        .discountRate(ir.getDiscountRate())
                        .userAdded(originator)
                        .build());
            }
        }
        order.setItems(items);

        // set status bid in history - first status item
        order.getOrderStatusHistory().add(OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatusTypes.BID)
                .userAdded(originator)
                .dateAdded(order.getDateAdded())
                .build());

        return iOrder.saveAndFlush(order);
    }

    @Transactional
    public void delete(final UserInfoDto userInfo, final Long orderId) {
        findByIdOrThrow(orderId);
        iOrder.deleteById(orderId);
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
