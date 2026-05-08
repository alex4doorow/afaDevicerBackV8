package com.afa.devicer.back.validators;

import com.afa.core.dto.orders.OrderConditionsDto;
import com.afa.core.dto.orders.OrderSaveRequest;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import com.afa.devicer.back.entities.customers.Customer;
import com.afa.devicer.back.entities.orders.IOrder;
import com.afa.devicer.back.entities.people.Person;
import com.afa.devicer.back.services.CustomerService;
import com.afa.devicer.back.services.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.CouplingBetweenObjects", "PMD.ExcessiveImports", "PMD.TooManyMethods",
        "PMD.GodClass", "PMD.CyclomaticComplexity"})
public class OrderValidator {

    private final CustomerService customerService;
    private final ProductCategoryService productCategoryService;
    private final IOrder iOrder;

    public void validateOrderCreating(final OrderSaveRequest request) {
        customerService.findByIdOrThrow(request.getCustomerId());
        productCategoryService.findByIdOrThrow(request.getProductCategoryId());
    }

    public void validateOrderEditing(
            final Long orderId,
            final Customer customer,
            final OrderSaveRequest request) {
        if (iOrder.existsByOrderNumAndIdNot(request.getOrderNum(), orderId)) {
            throw new DevicerException(DevicerErrors.ORDER_NUM_DUPLICATE, request.getOrderNum());
        }
        if (customer.getMainContact() == null) {
            throw new DevicerException(DevicerErrors.CUSTOMER_CONTACT_NOT_FOUND);
        }
    }

    public void validateOrderRecipient(
            final Person recipient,
            final OrderSaveRequest request) {
        if (recipient == null) {
            throw new DevicerException(DevicerErrors.ORDER_DELIVERY_RECIPIENT_NOT_FOUND,
                    request.getDelivery().getRecipient().getFirstName());
        }
    }

    public void validateFilterByList(final OrderConditionsDto filter) {

        if (filter.isPeriodExist() && filter.getPeriod() == null) {
            throw new DevicerException(DevicerErrors.ORDER_PERIOD_NOT_EXIST);
        }
    }

}