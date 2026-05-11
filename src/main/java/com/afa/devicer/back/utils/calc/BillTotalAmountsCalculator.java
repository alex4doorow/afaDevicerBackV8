package com.afa.devicer.back.utils.calc;

import com.afa.core.enums.AmountTypes;
import com.afa.devicer.back.entities.orders.Order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@SuppressWarnings({"PMD.LawOfDemeter"})
public class BillTotalAmountsCalculator extends BaseOrderTotalAmountsCalculator {

	public BillTotalAmountsCalculator(final Order order) {
		super(order);
	}

	@Override
	public Map<AmountTypes, BigDecimal> calc() {
					
		final BigDecimal deliveryCustomerPrice = order.getDelivery().getFactCustomerPrice();
        final BigDecimal deliverySellerPrice = order.getDelivery().getFactSellerPrice();
        final BigDecimal deltaDeliveryPrice = deliveryCustomerPrice.subtract(deliverySellerPrice);

        final Map<AmountTypes, BigDecimal> subTotals = calcSubTotals(order.getItems());

        final BigDecimal bill = subTotals.get(AmountTypes.BILL);
        final BigDecimal supplier = subTotals.get(AmountTypes.SUPPLIER);
		
		orderAmounts.put(AmountTypes.DELIVERY, deliveryCustomerPrice);

        final BigDecimal total = BigDecimal.ZERO.add(bill);
        final BigDecimal totalWithDelivery = bill.add(deliveryCustomerPrice);
		
		BigDecimal margin = bill.add(deltaDeliveryPrice).subtract(supplier);
		margin = margin.subtract(margin.multiply(new BigDecimal("0.15")));
		margin = margin.setScale(2, RoundingMode.HALF_UP);

        final BigDecimal postpay = order.getPostpayAmount();
		orderAmounts.put(AmountTypes.BILL, bill);
		orderAmounts.put(AmountTypes.TOTAL_WITH_DELIVERY, totalWithDelivery);
		orderAmounts.put(AmountTypes.TOTAL, total);
		orderAmounts.put(AmountTypes.MARGIN, margin);
		orderAmounts.put(AmountTypes.SUPPLIER, supplier);
		orderAmounts.put(AmountTypes.POSTPAY, postpay);

        return orderAmounts;
	}
}
