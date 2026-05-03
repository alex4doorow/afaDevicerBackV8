package com.afa.devicer.back.services;

import com.afa.core.dto.UserInfoDto;
import com.afa.core.dto.dictionaries.AddressSaveRequest;
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
import com.afa.devicer.back.utils.calc.AnyOrderTotalAmountsCalculator;
import com.afa.devicer.back.utils.calc.OrderTotalAmountsCalculatorFactory;
import com.afa.devicer.back.validators.OrderServiceValidator;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            final OrderPagedFilter request) {

        final OrderConditionsDto filter = request.getConditions();
        validator.validateFilterByList(filter);

        final Page<Order> page = iOrder.findAll(fillOrderSpecification(user, filter),
                request.createPageRequest(request.isSortedByEmpty() ? "id desc" : request.getSortedBy(),
                        Order_.class.getDeclaredFields()));

        final List<OrderDto> orderDtos = mapper.fromOrders(page.getContent());
        return new OrderPagedResponse(
                page.getTotalElements(), page.getTotalPages(),
                page.hasPrevious(), page.hasNext(),
                orderDtos,
                calcTotalOrdersAmounts(user, page.getContent(), filter.getPeriod()));
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

        return new OrderPagedResponse(orders.size(), 1, false, false, orderDtos,
                Collections.emptyMap());
    }


    @Transactional
    public Order create(
            final UserInfoDto userInfo,
            final OrderSaveRequest request) {

        validator.validateOrderCreating(request);

        final Country deliveryCountry = dictionaryCountryService.findByIdOrThrow(request.getDelivery().getAddress().getCountryId());
        final Person originator = userInfoService.findByIdOrThrow(userInfo.getPersonId());
        Person recipient = personService.findByPhoneNumber(request.getDelivery().getRecipient().getPhoneNumber());
        if (recipient == null) {

            final PersonSaveRequest recipientRequest = request.getDelivery().getRecipient();
            final Person recipientPerson = new Person();
            savePersonDataFromRequest(recipientPerson, recipientRequest, deliveryCountry);
            recipient = personService.create(recipientPerson);
        }

        Address deliveryAddress = new Address();
        saveAddressDataFromRequest(deliveryAddress, deliveryCountry, request.getDelivery().getAddress());
        deliveryAddress.setUserAdded(originator);
        deliveryAddress = addressService.create(deliveryAddress);

        final Order order = new Order();
        saveOrderDataFromRequest(order, request);
        order.setStatus(OrderStatusTypes.BID);
        order.setUserAdded(originator);

        final OrderDelivery delivery = new OrderDelivery();
        saveOrderDeliveryDataFromRequest(delivery, deliveryAddress, recipient, order, request.getDelivery());

        order.setItems(new HashSet<>());
        saveOrderItemsDataFromRequest(order, request, originator);

        addStatusHistory(order, OrderStatusTypes.BID, originator);

        return iOrder.saveAndFlush(order);
    }

    @Transactional
    public Order edit(final UserInfoDto userInfo,
                      final Long orderId,
                      final OrderSaveRequest request) {

        final Order order = findByIdOrThrow(orderId);
        final Person originator = userInfoService.findByIdOrThrow(userInfo.getPersonId());
        validator.validateOrderEditing(orderId, request);

        final Address deliveryAddress = order.getDelivery().getAddress();
        final Country deliveryCountry = dictionaryCountryService.findByIdOrThrow(request.getDelivery().getAddress().getCountryId());

        saveAddressDataFromRequest(deliveryAddress, deliveryCountry, request.getDelivery().getAddress());
        saveOrderDeliveryDataFromRequest(
                order.getDelivery(),
                deliveryAddress,
                order.getDelivery().getRecipient(),
                order,
                request.getDelivery());

        saveOrderDataFromRequest(order, request);
        order.setDateModified(Instant.now());

        order.clearItems();
        iOrder.flush();

        saveOrderItemsDataFromRequest(order, request, originator);
        saveTotalAmounts(order);

        return iOrder.saveAndFlush(order);
    }

    private void saveTotalAmounts(final Order order) {
        final AnyOrderTotalAmountsCalculator calculator = OrderTotalAmountsCalculatorFactory.createCalculator(order);
        final Map<AmountTypes, BigDecimal> amounts = calculator.calc();

        order.setBillAmount(amounts.get(AmountTypes.BILL));
        order.setTotalAmount(amounts.get(AmountTypes.TOTAL));
        order.setTotalWithDeliveryAmount(amounts.get(AmountTypes.TOTAL_WITH_DELIVERY));
        order.setMarginAmount(amounts.get(AmountTypes.MARGIN));
        order.setPostpayAmount(amounts.get(AmountTypes.POSTPAY));
        order.setSupplierAmount(amounts.get(AmountTypes.SUPPLIER));
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
            addStatusHistory(order, request.getStatus(), originator);
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
    private Specification<Order> fillOrderSpecification(final UserInfoDto user, final OrderConditionsDto filterConditions) {

        return (root, query, builder) -> {

            final List<Predicate> predicates = new ArrayList<>();

            // order
            if (filterConditions.getId() != null) {
                predicates.add(builder.equal(root.get(Order_.ID), filterConditions.getId()));
            }
            if (filterConditions.getOrderNum() != null) {
                predicates.add(builder.equal(root.get(Order_.ORDER_NUM), filterConditions.getOrderNum()));
            }
            if (StringUtils.isNoneBlank(filterConditions.getTrackCode())) {
                predicates.add(builder.equal(root.get(Order_.DELIVERY).get(OrderDelivery_.TRACK_CODE), filterConditions.getTrackCode()));
            }
            if (StringUtils.isNoneBlank(filterConditions.getDeliveryAddress())) {
                predicates.add(builder.equal(root.get(Order_.DELIVERY).get(OrderDelivery_.ADDRESS).get(Address_.ADDRESS_LINE), filterConditions.getDeliveryAddress()));
            }
            if (filterConditions.getTypes() != null && !filterConditions.getTypes().isEmpty()) {
                predicates.add(root.get(Order_.TYPE).in(filterConditions.getTypes()));
            }
            if (filterConditions.getAdvertTypes() != null && !filterConditions.getAdvertTypes().isEmpty()) {
                predicates.add(root.get(Order_.ADVERT_TYPE).in(filterConditions.getAdvertTypes()));
            }
            if (filterConditions.getPaymentTypes() != null && !filterConditions.getPaymentTypes().isEmpty()) {
                predicates.add(root.get(Order_.PAYMENT_TYPE).in(filterConditions.getPaymentTypes()));
            }
            if (filterConditions.getDeliveryTypes() != null && !filterConditions.getDeliveryTypes().isEmpty()) {
                predicates.add(root.get(Order_.DELIVERY).get(OrderDelivery_.DELIVERY_TYPE).in(filterConditions.getDeliveryTypes()));
            }
            if (filterConditions.getStatuses() != null && !filterConditions.getStatuses().isEmpty()) {
                predicates.add(root.get(Order_.STATUS).in(filterConditions.getStatuses()));
            }

            // customer
            if (filterConditions.getCustomerConditions() != null && filterConditions.getCustomerConditions().getCustomerTypes() != null && !filterConditions.getCustomerConditions().getCustomerTypes().isEmpty()) {
                predicates.add(builder.equal(root.get(Order_.CUSTOMER).get(Customer_.TYPE), filterConditions.getCustomerConditions().getCustomerTypes()));
            }
            // customer - person
            if (filterConditions.getCustomerConditions() != null && StringUtils.isNoneBlank(filterConditions.getCustomerConditions().getPersonPhoneNumber())) {

                predicates.add(builder.isNotNull(root.get(Order_.CUSTOMER).get(Customer_.PERSON)));
                predicates.add(builder.equal(root.get(Order_.CUSTOMER).get(Customer_.PERSON).get(Person_.PHONE_NUMBER),
                        filterConditions.getCustomerConditions().getPersonPhoneNumber()
                ));
            }
            // customer - company
            if (filterConditions.getCustomerConditions() != null && StringUtils.isNoneBlank(filterConditions.getCustomerConditions().getCompanyInn())) {

                predicates.add(builder.isNotNull(root.get(Order_.CUSTOMER).get(Customer_.COMPANY)));
                predicates.add(builder.equal(root.get(Order_.CUSTOMER).get(Customer_.COMPANY).get(Company_.INN),
                        filterConditions.getCustomerConditions().getCompanyInn()
                ));
            }
            // period
            if (filterConditions.isPeriodExist()) {
                // за установленный период
                predicates.add(builder.between(root.get(Order_.ORDER_DATE), filterConditions.getPeriod().getFirst(), filterConditions.getPeriod().getSecond()));
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
        results.put(AmountTypes.POSTPAY_CDEK, postpayAmounts.get(AmountTypes.POSTPAY_CDEK));
        results.put(AmountTypes.POSTPAY_POST, postpayAmounts.get(AmountTypes.POSTPAY_POST));
        results.put(AmountTypes.POSTPAY_COMPANY, postpayAmounts.get(AmountTypes.POSTPAY_COMPANY));
        results.put(AmountTypes.POSTPAY_YANDEX_MARKET, postpayAmounts.get(AmountTypes.POSTPAY_YANDEX_MARKET));
        results.put(AmountTypes.POSTPAY_OZON_MARKET, postpayAmounts.get(AmountTypes.POSTPAY_OZON_MARKET));
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
                .conditions(OrderConditionsDto.builder()
                        .period(postpayPeriod)
                        .periodExist(true)
                        .build())
                .build();

        final Page<Order> postpayPage = iOrder.findAll(fillOrderSpecification(user, postpayFilter.getConditions()),
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
        postpayAmounts.put(AmountTypes.POSTPAY_CDEK, cdekPostpayAmount);
        postpayAmounts.put(AmountTypes.POSTPAY_POST, postPostpayAmount);
        postpayAmounts.put(AmountTypes.POSTPAY_COMPANY, companyPostpayAmount);
        postpayAmounts.put(AmountTypes.POSTPAY_YANDEX_MARKET, yandexMarketPostpayAmount);
        postpayAmounts.put(AmountTypes.POSTPAY_OZON_MARKET, ozonMarketPostpayAmount);
        postpayAmounts.put(AmountTypes.POSTPAY_YANDEX_GO, yandexGoPostpayAmount);
        return postpayAmounts;
    }

    private void savePersonDataFromRequest(
            final Person person,
            final PersonSaveRequest request,
            final Country country) {
        person.setCountry(country);
        person.setFirstName(request.getFirstName());
        person.setMiddleName(request.getMiddleName());
        person.setLastName(request.getLastName());
        person.setPhoneNumber(request.getPhoneNumber());
        person.setEmail(request.getEmail());
    }

    private void saveAddressDataFromRequest(
            final Address deliveryAddress,
            final Country country,
            final AddressSaveRequest request) {
        deliveryAddress.setCountry(country);
        deliveryAddress.setType(AddressTypes.MAIN);
        deliveryAddress.setPostCode(request.getPostCode());
        deliveryAddress.setStreet(request.getStreet());
        deliveryAddress.setHouse(request.getHouse());
        deliveryAddress.setFlat(request.getFlat());
        deliveryAddress.setAddressLine(request.getAddressLine());
    }

    private void saveOrderDeliveryDataFromRequest(
            final OrderDelivery delivery,
            final Address deliveryAddress,
            final Person person,
            final Order order,
            final OrderDeliverySaveRequest request) {
        delivery.setOrder(order);
        delivery.setPrice(request.getPrice());
        delivery.setDeliveryType(request.getDeliveryType());
        delivery.setDeliveryPaymentType(request.getDeliveryPaymentType());
        delivery.setDeliveryPriceType(request.getDeliveryPriceType());
        delivery.setAddress(deliveryAddress);
        delivery.setRecipient(person);
        delivery.setAnnotation(request.getAnnotation());
        delivery.setDeliveryDate(request.getDeliveryDate());
        delivery.setTimeIn(request.getTimeIn());
        delivery.setTimeOut(request.getTimeOut());
        order.setDelivery(delivery);
    }

    private void saveOrderDataFromRequest(
            final Order order,
            final OrderSaveRequest request) {
        order.setOrderNum(request.getOrderNum());
        order.setOrderDate(request.getOrderDate());
        order.setCustomer(customerService.findByIdOrThrow(request.getCustomerId()));
        order.setType(request.getType());
        order.setSourceType(request.getSourceType());
        order.setAdvertType(request.getAdvertType());
        order.setPaymentType(request.getPaymentType());
        order.setStore(request.getStore());
        order.setProductCategory(productCategoryService.findByIdOrThrow(request.getProductCategoryId()));
        order.setAnnotation(request.getAnnotation());
    }

    private void addStatusHistory(
            final Order order,
            final OrderStatusTypes status,
            final Person originator) {
        final OrderStatusHistory statusHistory = new OrderStatusHistory();
        statusHistory.setOrder(order);
        statusHistory.setStatus(status);
        statusHistory.setUserAdded(originator);
        statusHistory.setDateAdded(Instant.now());
        order.getStatusHistory().add(statusHistory);
    }

    private void saveOrderItemsDataFromRequest(
            final Order order,
            final OrderSaveRequest request,
            final Person originator) {
        if (request.getItems() != null) {
            for (final OrderItemSaveRequest ir : request.getItems()) {
                final Product product = productService.findByIdOrThrow(ir.getProductId());
                final BigDecimal supplierPrice;
                if (product.getStock() == null) {
                    supplierPrice = BigDecimal.ZERO;
                } else {
                    supplierPrice = product.getStock().getSupplierPrice();
                }

                final BigDecimal quantity = BigDecimal.valueOf(ir.getQuantity());
                final BigDecimal baseAmount = ir.getPrice().multiply(quantity);
                final BigDecimal discount = baseAmount.multiply(ir.getDiscountRate()).divide(NumericHelper.ONE_HUNDRED,
                        RoundingMode.HALF_UP);
                final BigDecimal amount = baseAmount.subtract(discount);
                final BigDecimal supplierAmount = supplierPrice.multiply(quantity);

                final OrderItem item = new OrderItem();
                item.setItemNum(ir.getItemNum());
                item.setProduct(product);
                item.setPrice(ir.getPrice());
                item.setPriceSupplier(supplierPrice);
                item.setQuantity(ir.getQuantity());
                item.setDiscountRate(ir.getDiscountRate());
                item.setAmountSupplier(supplierAmount);
                item.setAmount(amount);
                item.setUserAdded(originator);

                order.addItem(item);
            }
        }
    }


}
