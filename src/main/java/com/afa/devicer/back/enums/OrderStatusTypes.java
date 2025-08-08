package com.afa.devicer.back.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
select * from oc_order_status where language_id = 2
		1 добавлен
		2 процесс оплаты
		17 передан в CRM
		19 подтвержден
		15 оплачен
		3 отправлен
		20 прибыл
		18 получен
		5 завершен
		16 анулирован
		12 возвращен
		11 возврат получен	
*/
@Getter
@AllArgsConstructor
public enum OrderStatusTypes {

	UNKNOWN(0, "неопределен", ""),
	BID(1, "заявка", "info"), // margin = 0, postpay = 0
    PROCESSING(21, "подтверждение", "info"), // margin = 0, postpay = 0
	UNPAID(22, "оформлен, не оплачен", "info"), // margin = 0, postpay = 0
	APPROVED(2, "подтвержден", ""),	// margin > 0, postpay > 0
	PAY_WAITING(3, "ожидаем оплату", "warning"), // margin = 0, postpay = ?
	PAY_ON(4, "оплата поступила", "warning"), // margin > 0, postpay = ?
	DELIVERING(5, "доставляется", ""), // margin > 0, postpay > 0
	READY_GIVE_AWAY(7, "прибыл", ""), // margin > 0, postpay > 0
	READY_GIVE_AWAY_TROUBLE(12, "заканчивается срок хранения", "danger"), // margin > 0, postpay > 0
	DELIVERED(10, "получен", ""),
	DOC_NOT_EXIST(11, "нет ТОРГ-12", ""), // margin > 0, postpay = 0
	FINISHED(8, "завершен", "success"), // margin > 0, postpay = 0
	REDELIVERY(9, "отказ от вручения", "secondary"), // margin = 0, postpay > 0
	CANCELED(13, "отменен", "danger"), // margin = 0, postpay = 0
	REDELIVERY_FINISHED(15, "возврат получен", "danger"), // --
	LOST(16, "утерян", "lost"); // margin = 0, postpay = 0

	private final int id;
	private final String annotation;
	private final String view;

}
