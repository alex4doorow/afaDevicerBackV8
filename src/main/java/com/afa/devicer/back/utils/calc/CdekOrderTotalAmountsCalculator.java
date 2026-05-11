package com.afa.devicer.back.utils.calc;

import com.afa.core.enums.AmountTypes;
import com.afa.core.enums.OrderPaymentTypes;
import com.afa.devicer.back.entities.orders.Order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@SuppressWarnings({"PMD.LawOfDemeter"})
public class CdekOrderTotalAmountsCalculator extends BaseOrderTotalAmountsCalculator {

	public CdekOrderTotalAmountsCalculator(final Order order) {
		super(order);
	}

	@Override
	public Map<AmountTypes, BigDecimal> calc() {
				
		BigDecimal deliveryCustomerPrice = order.getDelivery().getFactCustomerPrice();
		deliveryCustomerPrice = deliveryCustomerPrice == null ? BigDecimal.ZERO : deliveryCustomerPrice;

		BigDecimal deliverySellerPrice = order.getDelivery().getFactSellerPrice();				
		deliverySellerPrice = deliverySellerPrice == null ? BigDecimal.ZERO : deliverySellerPrice;

		final BigDecimal deltaDeliveryPrice = deliveryCustomerPrice.subtract(deliverySellerPrice);
						
		final Map<AmountTypes, BigDecimal> subTotals = calcSubTotals(order.getItems());
		
		final BigDecimal bill = subTotals.get(AmountTypes.BILL);
        final BigDecimal supplier = subTotals.get(AmountTypes.SUPPLIER);

        final BigDecimal total = BigDecimal.ZERO.add(bill);
        final BigDecimal totalWithDelivery = bill.add(deliveryCustomerPrice);

        BigDecimal margin = bill.subtract(supplier).add(deltaDeliveryPrice);
        margin = margin.subtract(margin.multiply(new BigDecimal("0.15")));
		margin = margin.setScale(2, RoundingMode.HALF_UP); 
		
		BigDecimal postpay = BigDecimal.ZERO;
		if (order.getPaymentType() == OrderPaymentTypes.POSTPAY) {
			postpay = totalWithDelivery.subtract(deliverySellerPrice);
		} 	
		orderAmounts.put(AmountTypes.DELIVERY, deliveryCustomerPrice);
		orderAmounts.put(AmountTypes.BILL, bill);
		orderAmounts.put(AmountTypes.TOTAL_WITH_DELIVERY, totalWithDelivery);
		orderAmounts.put(AmountTypes.TOTAL, total);
		orderAmounts.put(AmountTypes.MARGIN, margin);
		orderAmounts.put(AmountTypes.SUPPLIER, supplier);
		orderAmounts.put(AmountTypes.POSTPAY, postpay);
        orderAmounts.put(AmountTypes.TOTAL_WEIGHT_GRAM, subTotals.get(AmountTypes.TOTAL_WEIGHT_GRAM));
        orderAmounts.put(AmountTypes.TOTAL_WIDTH_CM, subTotals.get(AmountTypes.TOTAL_WIDTH_CM));
        orderAmounts.put(AmountTypes.TOTAL_LENGTH_CM, subTotals.get(AmountTypes.TOTAL_LENGTH_CM));
        orderAmounts.put(AmountTypes.TOTAL_HEIGHT_CM, subTotals.get(AmountTypes.TOTAL_HEIGHT_CM));
		return orderAmounts;
	}
}
