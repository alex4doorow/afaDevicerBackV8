package com.afa.devicer.back.utils.calc;

import com.afa.core.enums.OrderTypes;
import com.afa.devicer.back.entities.orders.Order;

@SuppressWarnings({"PMD.LawOfDemeter"})
public final class OrderTotalAmountsCalculatorFactory {

    private OrderTotalAmountsCalculatorFactory() {
    }

    public static AnyOrderTotalAmountsCalculator createCalculator(final Order order) {

		if (order.getType() == OrderTypes.BILL || order.getType() == OrderTypes.KP) {
            return new BillTotalAmountsCalculator(order);
        } else if (order.getType() == OrderTypes.ORDER) {
            return new CdekOrderTotalAmountsCalculator(order);
        } else {
			return new EmptyTotalAmountsCalculator(order);
		}
	}
}
