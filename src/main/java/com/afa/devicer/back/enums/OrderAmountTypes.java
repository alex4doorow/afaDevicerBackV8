package com.afa.devicer.back.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderAmountTypes {
		
	UNKNOWN(0, "не определена"),
	TOTAL_WITH_DELIVERY(1, "итоговая сумма к оплате клиенту (равна итоговой детализации + стоимости доставки)"),
	BILL(2, "сумма чека (наш доход)"),
	SUPPLIER(3, "стоимость закупки"),
	MARGIN(4, "прибыль за вычетом налогов (amount_bill - amount_supplier)"),
	POSTPAY(5, "стоимость постоплаты"),
	// только для отчетов
	TOTAL(6, "итоговая сумма за товар"),
	DELIVERY(7, "стоимость доставки"),
	CASH_ON_DELIVERY(8, "наложенный платеж"),
	
	ADVERT_BUDGET(101, "рекламный бюджет за месяц"),
	COUNT_VISITS(102, "число сеансов за период"),
	COUNT_UNIQUE_VISITORS(107, "число уникальных посетителей за период"),
	COUNT_NEW_VISITORS(111, "число новых посетителей за период"),	
	COUNT_REAL_ORDERS(106, "число реальных заказов за период"),	
	CONVERSION_APPROVED(127, "конверсия (заказы/сеансы)"), 
	CONVERSION_BID(128, "конверсия (заявки/сеансы)"),
	POSTPAY_SDEK(108, "стоимость постоплаты СДЭК"),
	POSTPAY_POST(109, "стоимость постоплаты почта"),
	POSTPAY_COMPANY(110, "стоимость постоплаты организаций"),
	POSTPAY_YANDEX_MARKET(131, "стоимость постоплаты \"Яндекс.Маркет\""),
	POSTPAY_OZON_MARKET(112, "стоимость постоплаты \"OZON\""),
	POSTPAY_OZON_ROCKET(113, "стоимость постоплаты \"OZON ROCKET\""),
	POSTPAY_YANDEX_GO(114, "стоимость постоплаты \"YANDEX GO\""),
	ADVERT_WEEK_BUDGET(132, "рекламный бюджет за неделю"),
		
	// total amounts
	TOTAL_SUPPLIER(121, "суммарный опт"),
	TOTAL_BILL(122, "суммарная розница");	
		
	private final int id;
    private final String annotation;

}
