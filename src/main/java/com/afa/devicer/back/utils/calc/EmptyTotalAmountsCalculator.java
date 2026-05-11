package com.afa.devicer.back.utils.calc;

import com.afa.core.enums.AmountTypes;
import com.afa.devicer.back.entities.orders.Order;

import java.math.BigDecimal;
import java.util.Map;

@SuppressWarnings({"PMD.LawOfDemeter"})
public class EmptyTotalAmountsCalculator extends BaseOrderTotalAmountsCalculator {
	
	public EmptyTotalAmountsCalculator(final Order order) {
		super(order);
	}

	@Override
	public Map<AmountTypes, BigDecimal> calc() {
		
		orderAmounts.put(AmountTypes.BILL, BigDecimal.ZERO);
		orderAmounts.put(AmountTypes.TOTAL_WITH_DELIVERY, BigDecimal.ZERO);
		orderAmounts.put(AmountTypes.MARGIN, BigDecimal.ZERO);
		orderAmounts.put(AmountTypes.SUPPLIER, BigDecimal.ZERO);
		orderAmounts.put(AmountTypes.POSTPAY, BigDecimal.ZERO);
		return orderAmounts;
	}

}
