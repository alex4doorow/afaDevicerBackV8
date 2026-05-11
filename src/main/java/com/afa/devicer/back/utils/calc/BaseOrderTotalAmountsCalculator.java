package com.afa.devicer.back.utils.calc;

import com.afa.core.enums.AmountTypes;
import com.afa.devicer.back.entities.orders.Order;
import com.afa.devicer.back.entities.orders.OrderItem;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"PMD.LawOfDemeter"})
public abstract class BaseOrderTotalAmountsCalculator {
		
	protected final Order order;
    protected final Map<AmountTypes, BigDecimal> orderAmounts = new HashMap<>();

	public BaseOrderTotalAmountsCalculator(final Order order) {
		this.order = order;
	}
	
	public abstract Map<AmountTypes, BigDecimal> calc();
	
	protected Map<AmountTypes, BigDecimal> calcSubTotals(final Set<OrderItem> orderItems) {

		final Map<AmountTypes, BigDecimal> result = new HashMap<>();
        BigDecimal totalWeightGram = BigDecimal.ZERO;
		BigDecimal bill = BigDecimal.ZERO;
		BigDecimal supplier = BigDecimal.ZERO;
		for (final OrderItem item : orderItems) {
			if (item.getAmount() != null) {
				bill = bill.add(item.getAmount());
			} else if (item.getPrice() != null) {
                final BigDecimal amount = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                item.setAmount(amount);
                bill = bill.add(item.getAmount());
            }
			if (item.getSupplierPrice() != null) {
				supplier = supplier.add(item.getSupplierPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
			}
            if (item.getProduct() != null) {
                // todo calculator gramm|kilogramm
                final BigDecimal itemWeightGram = item.getProduct().getWeight().multiply(BigDecimal.valueOf(item.getQuantity()));
                totalWeightGram = totalWeightGram.add(itemWeightGram);
            }
		}
		result.put(AmountTypes.BILL, bill);
		result.put(AmountTypes.SUPPLIER, supplier);
        if (totalWeightGram.compareTo(BigDecimal.ZERO) <= 0) {
            totalWeightGram = BigDecimal.valueOf(500);
        }

        result.put(AmountTypes.TOTAL_WEIGHT_GRAM, totalWeightGram);
        result.put(AmountTypes.TOTAL_WIDTH_CM, BigDecimal.valueOf(30));
        result.put(AmountTypes.TOTAL_HEIGHT_CM, BigDecimal.valueOf(30));
        result.put(AmountTypes.TOTAL_LENGTH_CM, BigDecimal.valueOf(30));

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
