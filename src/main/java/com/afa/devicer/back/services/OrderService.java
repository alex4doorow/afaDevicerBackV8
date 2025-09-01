package com.afa.devicer.back.services;

import com.afa.core.dto.UserInfoDto;
import com.afa.core.dto.orders.*;
import com.afa.core.dto.persons.PersonSaveRequest;
import com.afa.core.enums.*;
import com.afa.core.exceptions.DevicerException;
import com.afa.core.utils.DateHelper;
import com.afa.core.utils.NumericHelper;
import com.afa.devicer.back.entities.companies.Company_;
import com.afa.devicer.back.entities.customers.Customer_;
import com.afa.devicer.back.entities.dictionaries.Address;
import com.afa.devicer.back.entities.dictionaries.Address_;
import com.afa.devicer.back.entities.dictionaries.Country;
import com.afa.devicer.back.entities.orders.*;
import com.afa.devicer.back.entities.people.Person;
import com.afa.devicer.back.entities.people.Person_;
import com.afa.devicer.back.entities.products.Product;
import com.afa.devicer.back.mappers.OrderMapper;
import com.afa.devicer.back.validators.OrderServiceValidator;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.CouplingBetweenObjects", "PMD.ExcessiveImports", "PMD.TooManyMethods",
        "PMD.GodClass", "PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
public class OrderService {

    private final IOrder iOrder;

    private final UserInfoService userInfoService;
    private final CustomerService customerService;
    private final ProductCategoryService productCategoryService;
    private final ProductService productService;

    private final PersonService personService;
    private final AddressService addressService;

    private final DictionaryCountryService dictionaryCountryService;
    private final PeriodTotalAmountService periodTotalAmountService;
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

    @SuppressWarnings("PMD.UnnecessaryLocalBeforeReturn")
    public OrderPagedResponse getFiltered(
            final UserInfoDto user,
            final OrderPagedFilter filter) {

        validator.validateFilterByList(filter);

        final Page<Order> page = iOrder.findAll(fillOrderSpecification(user, filter),
                filter.createPageRequest(filter.isSortedByEmpty() ? "id desc" : filter.getSortedBy(),
                        Order_.class.getDeclaredFields()));

        final List<OrderDto> orderDtos = mapper.fromOrders(page.getContent());
        for (final OrderDto dto : orderDtos) {
            dto.setPresentationStatus(OrderPresentationStatusDto.createOrderPresentationStatusDto(dto));
        }
        return new OrderPagedResponse(
                page.getTotalElements(), page.getTotalPages(),
                page.hasPrevious(), page.hasNext(),
                orderDtos,
                calcTotalOrdersAmounts(user, page.getContent(), filter.getPeriod()));
    }

    private Map<AmountTypes, BigDecimal> calcTotalOrdersAmounts(
            @NotNull final UserInfoDto user,
            final List<Order> orders,
            final Pair<LocalDate, LocalDate> period) {

        final Map<AmountTypes, BigDecimal> results = new HashMap<>();

        BigDecimal billAmount = BigDecimal.ZERO;
        BigDecimal supplierAmount = BigDecimal.ZERO;
        BigDecimal marginAmount = BigDecimal.ZERO;
        BigDecimal approvedConversion = BigDecimal.ZERO;
        BigDecimal bidConversion = BigDecimal.ZERO;
        int realOrdersCount = 0;

        for (final Order order : orders) {
            if (order.isBillAmount()) {
                realOrdersCount++;
                billAmount = billAmount.add(order.getBillAmount());
                supplierAmount = supplierAmount.add(order.getSupplierAmount());
                marginAmount = marginAmount.add(order.getMarginAmount());
            }
        }

        final BigDecimal advertAmount = periodTotalAmountService.ejectTotalAmountsByConditions(AmountTypes.ADVERT_BUDGET, period);
        final BigDecimal clickCount = periodTotalAmountService.ejectTotalAmountsByConditions(AmountTypes.COUNT_VISITS, period);

        if (clickCount != null && !clickCount.equals(BigDecimal.ZERO)) {
            approvedConversion = BigDecimal.valueOf(realOrdersCount).divide(clickCount, 4, RoundingMode.HALF_UP);
            bidConversion = BigDecimal.valueOf(orders.size()).divide(clickCount, 4, RoundingMode.HALF_UP);
        }
        final BigDecimal marginWithoutAdvertAmount = marginAmount.subtract(advertAmount);
        results.put(AmountTypes.BILL, billAmount);
        results.put(AmountTypes.SUPPLIER, supplierAmount);
        results.put(AmountTypes.MARGIN, marginWithoutAdvertAmount);
        final Map<AmountTypes, BigDecimal> postpayAmounts = calcTotalOrdersPostpayAmountByConditions(user);

        results.put(AmountTypes.POSTPAY, postpayAmounts.get(AmountTypes.POSTPAY));
        results.put(AmountTypes.POSTPAY_SDEK, postpayAmounts.get(AmountTypes.POSTPAY_SDEK));
        results.put(AmountTypes.POSTPAY_POST, postpayAmounts.get(AmountTypes.POSTPAY_POST));
        results.put(AmountTypes.POSTPAY_COMPANY, postpayAmounts.get(AmountTypes.POSTPAY_COMPANY));
        results.put(AmountTypes.POSTPAY_YANDEX_MARKET, postpayAmounts.get(AmountTypes.POSTPAY_YANDEX_MARKET));
        results.put(AmountTypes.POSTPAY_OZON_MARKET, postpayAmounts.get(AmountTypes.POSTPAY_OZON_MARKET));
        results.put(AmountTypes.POSTPAY_OZON_ROCKET, postpayAmounts.get(AmountTypes.POSTPAY_OZON_ROCKET));
        results.put(AmountTypes.POSTPAY_YANDEX_GO, postpayAmounts.get(AmountTypes.POSTPAY_YANDEX_GO));

        results.put(AmountTypes.ADVERT_BUDGET, advertAmount);
        results.put(AmountTypes.COUNT_REAL_ORDERS, BigDecimal.valueOf(realOrdersCount));
        results.put(AmountTypes.CONVERSION_APPROVED, approvedConversion);
        results.put(AmountTypes.CONVERSION_BID, bidConversion);
        return results;
    }

    private Map<AmountTypes, BigDecimal> calcTotalOrdersPostpayAmountByConditions(
            @NotNull final UserInfoDto user) {

        LocalDate minOrderDate = iOrder.findMinOrderDateWithPostpayExcludingStatuses(List.of(
                OrderStatusTypes.UNKNOWN,
                OrderStatusTypes.BID,
                OrderStatusTypes.PROCESSING,
                OrderStatusTypes.UNPAID,
                OrderStatusTypes.APPROVED,
                OrderStatusTypes.PAY_ON,
                OrderStatusTypes.FINISHED,
                OrderStatusTypes.CANCELED,
                OrderStatusTypes.REDELIVERY_FINISHED,
                OrderStatusTypes.LOST,
                OrderStatusTypes.DOC_NOT_EXIST));

        if (minOrderDate == null) {
            minOrderDate = DateHelper.firstDayOfYear(LocalDate.now());
        }

        final Map<AmountTypes, BigDecimal> postpayAmounts = new HashMap<>();
        BigDecimal postpayAmount = BigDecimal.ZERO;
        BigDecimal cdekPostpayAmount = BigDecimal.ZERO;
        BigDecimal postPostpayAmount = BigDecimal.ZERO;
        BigDecimal companyPostpayAmount = BigDecimal.ZERO;
        BigDecimal yandexMarketPostpayAmount = BigDecimal.ZERO;
        BigDecimal ozonMarketPostpayAmount = BigDecimal.ZERO;
        BigDecimal yandexGoPostpayAmount = BigDecimal.ZERO;

        final Pair<LocalDate, LocalDate> postpayPeriod = Pair.of(minOrderDate, DateHelper.lastDayOfMonth(LocalDate.now()));
        final OrderPagedFilter postpayFilter = OrderPagedFilter.builder()
                .period(postpayPeriod)
                .periodExist(true)
                .build();

        final Page<Order> postpayPage = iOrder.findAll(fillOrderSpecification(user, postpayFilter),
                postpayFilter.createPageRequest(postpayFilter.isSortedByEmpty() ? "id desc" : postpayFilter.getSortedBy(),
                        Order_.class.getDeclaredFields()));

        for (final Order order : postpayPage.getContent()) {
            if (order.isPostpayAmount()) {

                postpayAmount = postpayAmount.add(order.getPostpayAmount());

                if (order.getAdvertType() == OrderAdvertTypes.YANDEX_MARKET) {
                    yandexMarketPostpayAmount = yandexMarketPostpayAmount.add(order.getPostpayAmount());
                } else if (order.getAdvertType() == OrderAdvertTypes.OZON) {
                    ozonMarketPostpayAmount = ozonMarketPostpayAmount.add(order.getPostpayAmount());
                } else if (order.getCustomer().isPerson() && (order.getDelivery().getDeliveryType().isCdek() || order.getDelivery().getDeliveryType() == DeliveryTypes.PICKUP)) {
                    cdekPostpayAmount = cdekPostpayAmount.add(order.getPostpayAmount());
                } else if (order.getCustomer().isPerson() && order.getDelivery().getDeliveryType().isPost()) {
                    postPostpayAmount = postPostpayAmount.add(order.getPostpayAmount());
                } else if (order.getCustomer().isPerson() && order.getDelivery().getDeliveryType() == DeliveryTypes.YANDEX_GO) {
                    yandexGoPostpayAmount = postPostpayAmount.add(order.getPostpayAmount());
                } else if (order.getCustomer().isCompany()) {
                    companyPostpayAmount = companyPostpayAmount.add(order.getPostpayAmount());
                } else {
                    cdekPostpayAmount = cdekPostpayAmount.add(order.getPostpayAmount());
                }
                log.debug("postpay: {}, {}, {}, [sdek:{}, post:{}, company:{}, ym:{}, ozon:{}, yGo:{}]", order.getOrderNum(), order.getCustomer().getViewShortName(), order.getPostpayAmount(),
                        cdekPostpayAmount, postPostpayAmount, companyPostpayAmount,
                        yandexMarketPostpayAmount, ozonMarketPostpayAmount, yandexGoPostpayAmount);
            }
        }

        postpayAmounts.put(AmountTypes.POSTPAY, postpayAmount);
        postpayAmounts.put(AmountTypes.POSTPAY_SDEK, cdekPostpayAmount);
        postpayAmounts.put(AmountTypes.POSTPAY_POST, postPostpayAmount);
        postpayAmounts.put(AmountTypes.POSTPAY_COMPANY, companyPostpayAmount);
        postpayAmounts.put(AmountTypes.POSTPAY_YANDEX_MARKET, yandexMarketPostpayAmount);
        postpayAmounts.put(AmountTypes.POSTPAY_OZON_MARKET, ozonMarketPostpayAmount);
        postpayAmounts.put(AmountTypes.POSTPAY_YANDEX_GO, yandexGoPostpayAmount);
        return postpayAmounts;
    }

    @SuppressWarnings("PMD.UnnecessaryLocalBeforeReturn")
    public OrderPagedResponse getSimpleFiltered(
            final UserInfoDto user,
            final String dirtyConditions) {

        if (StringUtils.isEmpty(dirtyConditions)) {
            return new OrderPagedResponse(0, 1, false, false, Collections.emptyList(), Collections.emptyMap());
        }
        log.debug("getSimpleFiltered(): {}", dirtyConditions);

        final List<Order> orders = iOrder.findAll(fillOrderSpecificationBySimpleConditions(user, dirtyConditions.trim()));
        final List<OrderDto> orderDtos = mapper.fromOrders(orders);

        for (final OrderDto dto : orderDtos) {
            dto.setPresentationStatus(OrderPresentationStatusDto.createOrderPresentationStatusDto(dto));
        }
        return new OrderPagedResponse(orders.size(), 1, false, false, orderDtos,
                Collections.emptyMap());
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

            for (final OrderItemSaveRequest ir : request.getItems()) {
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
        order.getStatusHistory().add(OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatusTypes.BID)
                .userAdded(originator)
                .dateAdded(Instant.now())
                .build());

        return iOrder.saveAndFlush(order);
    }

    @Transactional
    public Order edit(final UserInfoDto userInfo,
                      final Long orderId,
                      final OrderSaveRequest request) {
        final Order order = findByIdOrThrow(orderId);

        //

        return iOrder.saveAndFlush(order);
    }

    @SuppressWarnings("PMD")
    @Transactional
    public Order changeFullStatus(final UserInfoDto userInfo,
                                  final Long orderId,
                                  final OrderChangeStatusSaveRequest request) {
        final Order order = findByIdOrThrow(orderId);
        final OrderStatusTypes currentStatus = order.getStatus();

        final Person originator = userInfoService.findByIdOrThrow(userInfo.getPersonId());

        order.setType(request.getType());
        order.setSourceType(request.getSourceType());
        order.setPaymentType(request.getPaymentType());
        order.setProductCategory(productCategoryService.findByIdOrThrow(request.getProductCategoryId()));
        order.setStatus(request.getStatus());
        order.setAnnotation(request.getAnnotation());

        order.getDelivery().setTrackCode(request.getTrackCode());

        if (currentStatus != request.getStatus()) {
            order.getStatusHistory().add(OrderStatusHistory.builder()
                    .order(order)
                    .status(request.getStatus())
                    .userAdded(originator)
                    .dateAdded(Instant.now())
                    .build());
        }

        if (request.getStatus() == OrderStatusTypes.APPROVED) {
            operateSubstructProductQuantityOrder(order, OrderStatusTypes.APPROVED);
        }

        if (request.getStatus() == OrderStatusTypes.BID && currentStatus == OrderStatusTypes.CANCELED) {
            if (order.getType() == OrderTypes.BILL) {

//                final String sqlUpdateOrderOffer = "UPDATE sr_order"
//                        + "  SET offer_date_start = ?"
//                        + "  WHERE id = ?";
//                this.jdbcTemplate.update(sqlUpdateOrderOffer, new Object[]{
//                        DateTimeUtils.sysDate(),
//                        order.getId()});
            }
        }

        return iOrder.saveAndFlush(order);
    }

    @Transactional
    public void delete(final UserInfoDto userInfo, final Long orderId) {
        findByIdOrThrow(orderId);
        iOrder.deleteById(orderId);
    }

    @SuppressWarnings({"PMD.NPathComplexity", "PMD.UnusedFormalParameter"})
    private Specification<Order> fillOrderSpecificationBySimpleConditions(
            final UserInfoDto user,
            final String dirtyConditions) {

        return (root, query, builder) -> {

            final List<Predicate> predicates = new ArrayList<>();
            List<Order> orders;

            // 1) ищем по номеру заказа
            if (NumericHelper.isNumeric(dirtyConditions)) {
                orders = iOrder.findByOrderNum(Long.valueOf(dirtyConditions));
                if (!orders.isEmpty()) {
                    predicates.add(builder.equal(root.get(Order_.ORDER_NUM), Long.valueOf(dirtyConditions)));
                    return builder.and(predicates.toArray(new Predicate[0]));
                }
            }

            // 2) ищем по трэккоду
            orders = iOrder.findByDeliveryTrackCode(dirtyConditions);
            if (!orders.isEmpty()) {
                predicates.add(builder.equal(root.get(Order_.DELIVERY).get(OrderDelivery_.TRACK_CODE), dirtyConditions));
                return builder.and(predicates.toArray(new Predicate[0]));
            }
            // 3) ищем по телефону физика
            orders = iOrder.findByCustomerPersonIsNotNullAndCustomerPersonPhoneNumber(dirtyConditions);
            if (!orders.isEmpty()) {
                predicates.add(builder.isNotNull(root.get(Order_.CUSTOMER).get(Customer_.PERSON)));
                predicates.add(builder.equal(root.get(Order_.CUSTOMER).get(Customer_.PERSON).get(Person_.PHONE_NUMBER), dirtyConditions));
                return builder.and(predicates.toArray(new Predicate[0]));
            }

            // 4) ищем по email физика
            orders = iOrder.findByCustomerPersonIsNotNullAndCustomerPersonEmail(dirtyConditions);
            if (!orders.isEmpty()) {
                predicates.add(builder.isNotNull(root.get(Order_.CUSTOMER).get(Customer_.PERSON)));
                predicates.add(builder.equal(root.get(Order_.CUSTOMER).get(Customer_.PERSON).get(Person_.EMAIL), dirtyConditions));
                return builder.and(predicates.toArray(new Predicate[0]));
            }

            // 5) ищем по email юрика
            orders = iOrder.findByCustomerCompanyIsNotNullAndCustomerCompanyEmail(dirtyConditions);
            if (!orders.isEmpty()) {
                predicates.add(builder.isNotNull(root.get(Order_.CUSTOMER).get(Customer_.COMPANY)));
                predicates.add(builder.equal(root.get(Order_.CUSTOMER).get(Customer_.COMPANY).get(Company_.EMAIL), dirtyConditions));
                return builder.and(predicates.toArray(new Predicate[0]));
            }

            // 6) ищем по телефону юрика
            orders = iOrder.findByCustomerCompanyIsNotNullAndCustomerCompanyPhoneNumber(dirtyConditions);
            if (!orders.isEmpty()) {
                predicates.add(builder.isNotNull(root.get(Order_.CUSTOMER).get(Customer_.COMPANY)));
                predicates.add(builder.equal(root.get(Order_.CUSTOMER).get(Customer_.COMPANY).get(Company_.PHONE_NUMBER), dirtyConditions));
                return builder.and(predicates.toArray(new Predicate[0]));
            }

            // 7) ищем по opencart no

            // 8) ищем по наименованию компании


            // ничего не нашли - возвращаем фильтр для пустого списка
            predicates.add(builder.lessThan(root.get(Order_.ID), 0L));
            return builder.and(predicates.toArray(new Predicate[0]));
        };

    }

    @SuppressWarnings({"PMD.NPathComplexity", "PMD.UnusedFormalParameter", "PMD.EmptyControlStatement"})
    private Specification<Order> fillOrderSpecification(final UserInfoDto user, final OrderPagedFilter filter) {

        return (root, query, builder) -> {

            final List<Predicate> predicates = new ArrayList<>();

            // order
            if (filter.getId() != null) {
                predicates.add(builder.equal(root.get(Order_.ID), filter.getId()));
            }
            if (filter.getOrderNum() != null) {
                predicates.add(builder.equal(root.get(Order_.ORDER_NUM), filter.getOrderNum()));
            }
            if (StringUtils.isNoneBlank(filter.getTrackCode())) {
                predicates.add(builder.equal(root.get(Order_.DELIVERY).get(OrderDelivery_.TRACK_CODE), filter.getTrackCode()));
            }
            if (StringUtils.isNoneBlank(filter.getDeliveryAddress())) {
                predicates.add(builder.equal(root.get(Order_.DELIVERY).get(OrderDelivery_.ADDRESS).get(Address_.ADDRESS_LINE), filter.getDeliveryAddress()));
            }
            if (filter.getTypes() != null && !filter.getTypes().isEmpty()) {
                predicates.add(root.get(Order_.TYPE).in(filter.getTypes()));
            }
            if (filter.getAdvertTypes() != null && !filter.getAdvertTypes().isEmpty()) {
                predicates.add(root.get(Order_.ADVERT_TYPE).in(filter.getAdvertTypes()));
            }
            if (filter.getPaymentTypes() != null && !filter.getPaymentTypes().isEmpty()) {
                predicates.add(root.get(Order_.PAYMENT_TYPE).in(filter.getPaymentTypes()));
            }
            if (filter.getDeliveryTypes() != null && !filter.getDeliveryTypes().isEmpty()) {
                predicates.add(root.get(Order_.DELIVERY).get(OrderDelivery_.DELIVERY_TYPE).in(filter.getDeliveryTypes()));
            }
            if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) {
                predicates.add(root.get(Order_.STATUS).in(filter.getStatuses()));
            }

            // customer
            if (filter.getCustomerConditions() != null && filter.getCustomerConditions().getCustomerTypes() != null && !filter.getCustomerConditions().getCustomerTypes().isEmpty()) {
                predicates.add(builder.equal(root.get(Order_.CUSTOMER).get(Customer_.TYPE), filter.getCustomerConditions().getCustomerTypes()));
            }
//            if (filter.getCustomerConditions().getCountries() != null && !filter.getCustomerConditions().getCountries().isEmpty()) {
//                predicates.add(builder.equal(root.get(Order_.CUSTOMER).get(Customer_.COMPANY), filter.getCustomerConditions().getCustomerTypes()));
//            }
            // customer - person
            if (filter.getCustomerConditions() != null && StringUtils.isNoneBlank(filter.getCustomerConditions().getPersonPhoneNumber())) {

                predicates.add(builder.isNotNull(root.get(Order_.CUSTOMER).get(Customer_.PERSON)));
                predicates.add(builder.equal(root.get(Order_.CUSTOMER).get(Customer_.PERSON).get(Person_.PHONE_NUMBER),
                        filter.getCustomerConditions().getPersonPhoneNumber()
                ));
            }
            // customer - company
            if (filter.getCustomerConditions() != null && StringUtils.isNoneBlank(filter.getCustomerConditions().getCompanyInn())) {

                predicates.add(builder.isNotNull(root.get(Order_.CUSTOMER).get(Customer_.COMPANY)));
                predicates.add(builder.equal(root.get(Order_.CUSTOMER).get(Customer_.COMPANY).get(Company_.INN),
                        filter.getCustomerConditions().getCompanyInn()
                ));
            }

            // period
            if (filter.isPeriodExist()) {
                // за установленный период
                predicates.add(builder.between(root.get(Order_.ORDER_DATE), filter.getPeriod().getFirst(), filter.getPeriod().getSecond()));


            } else {
                // без учета периода
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Операция по расходу товаров с полок нашего склада при подтверждении заказа
     *
     * @param order
     */
    public void operateSubstructProductQuantityOrder(final Order order, final OrderStatusTypes phase) {

        for (final OrderItem orderItem : order.getItems()) {
            productService.updateDeltaQuantityProduct(orderItem.getProduct(), orderItem.getQuantity(), order.getExternalCrm(), phase);
        }

    }

}
