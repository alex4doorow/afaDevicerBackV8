package com.afa.devicer.back.utils.calc;

import com.afa.core.enums.AmountTypes;
import com.afa.devicer.back.entities.orders.Order;
import com.afa.devicer.back.entities.orders.OrderItem;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"PMD.LawOfDemeter"})
public abstract class AnyOrderTotalAmountsCalculator {
		
	protected final Order order;
    protected final Map<AmountTypes, BigDecimal> orderAmounts = new HashMap<>();

	public AnyOrderTotalAmountsCalculator(final Order order) {
		this.order = order;
	}
	
	public abstract Map<AmountTypes, BigDecimal> calc();
	
	protected Map<AmountTypes, BigDecimal> calcSubTotals(final Set<OrderItem> orderItems) {
		final Map<AmountTypes, BigDecimal> result = new HashMap<>();
		
		BigDecimal bill = BigDecimal.ZERO;
		BigDecimal supplier = BigDecimal.ZERO;
		for (final OrderItem item : orderItems) {
			if (item.getAmount() != null) {
				bill = bill.add(item.getAmount());
			}	
			if (item.getAmountSupplier() != null) {
				supplier = supplier.add(item.getPriceSupplier().multiply(BigDecimal.valueOf(item.getQuantity())));
			}
		}
		result.put(AmountTypes.BILL, bill);
		result.put(AmountTypes.SUPPLIER, supplier);
		
		BigDecimal deliveryPrice = order.getDelivery().getPrice();	
		if (deliveryPrice == null) {
			deliveryPrice = BigDecimal.ZERO;
		}
		setUnionAmounts(bill, deliveryPrice);		
		return result;
		
	}
	
	private void setUnionAmounts(final BigDecimal total, final BigDecimal deliveryPrice) {
        orderAmounts.put(AmountTypes.TOTAL, total);
        orderAmounts.put(AmountTypes.DELIVERY, deliveryPrice);
	}

}
