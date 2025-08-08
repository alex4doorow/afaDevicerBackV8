package com.afa.devicer.back.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CarrierStatuses {
		
	UNKNOWN(0, "неопределен", OrderStatusTypes.UNKNOWN),
	
	CDEK_CREATE(1, "Создан", OrderStatusTypes.BID),
	CDEK_DELETE(2, "Удален", OrderStatusTypes.CANCELED),	
	CDEK_PUSH_PVZ_SENDER(3, "Принят на склад отправителя", OrderStatusTypes.DELIVERING),	
	CDEK_DELIVERING_06(6, "Выдан на отправку в г. отправителе", OrderStatusTypes.DELIVERING),
	CDEK_DELIVERING_16(16, "Возвращен на склад отправителя", OrderStatusTypes.DELIVERING),	
	CDEK_DELIVERING_21(21, "Отправлен в г. транзит", OrderStatusTypes.DELIVERING),
	CDEK_DELIVERING_22(22, "Встречен в г. транзите", OrderStatusTypes.DELIVERING),
	CDEK_DELIVERING_13(13, "Принят на склад транзита", OrderStatusTypes.DELIVERING),
	CDEK_DELIVERING_17(17, "Возвращен на склад транзита", OrderStatusTypes.DELIVERING),
	CDEK_DELIVERING_19(19, "Выдан на отправку в г. транзите", OrderStatusTypes.DELIVERING),
	CDEK_DELIVERING_20(20, "Сдан перевозчику в г. транзите", OrderStatusTypes.DELIVERING),		
	CDEK_DELIVERING_07(7, "Сдан перевозчику в г. отправителе", OrderStatusTypes.DELIVERING),
	CDEK_DELIVERING_08(8, "Отправлен в г. получателя", OrderStatusTypes.DELIVERING),
	CDEK_DELIVERING_09(9, "Встречен в г. получателя", OrderStatusTypes.DELIVERING),	
	CDEK_READY_GIVE_AWAY_10(10, "Принят на склад доставки", OrderStatusTypes.READY_GIVE_AWAY),
	CDEK_READY_GIVE_AWAY_12(12, "Принят на склад до востребования", OrderStatusTypes.READY_GIVE_AWAY),
	CDEK_READY_GIVE_AWAY_11(11, "Выдан на доставку", OrderStatusTypes.READY_GIVE_AWAY),
	CDEK_READY_GIVE_AWAY_18(18, "Возвращен на склад доставки", OrderStatusTypes.READY_GIVE_AWAY),	
	CDEK_DELIVERED(4, "Вручен", OrderStatusTypes.DELIVERED),	
	CDEK_REDELIVERY(5, "Не вручен", OrderStatusTypes.REDELIVERY),
	
	OZON_CREATED(101, "Created", CrmTypes.OZON, "Создан", OrderStatusTypes.BID),
	OZON_REGISTERD(102, "Registered", CrmTypes.OZON, "Зарегистрирован", OrderStatusTypes.BID),	
	OZON_SENT(103, "Sent", CrmTypes.OZON, "Отправлен", OrderStatusTypes.DELIVERING),
	OZON_DELIVERING(104, "Delivering", CrmTypes.OZON, "Доставляется", OrderStatusTypes.DELIVERING),
	OZON_ARRIVED_TO_CITY(105, "ArrivedToCity", CrmTypes.OZON, "Прибыл в ваш город", OrderStatusTypes.DELIVERING),
	OZON_READY_FOR_PICKUP(106, "ReadyForPickup", CrmTypes.OZON, "Готов к выдаче", OrderStatusTypes.READY_GIVE_AWAY),
	OZON_DELIVERING_TO_CLIENT(107, "DeliveringToClient", CrmTypes.OZON, "Выехал к клиенту", OrderStatusTypes.DELIVERING),
	OZON_DELIVERED(108, "Delivered", CrmTypes.OZON, "Доставлен", OrderStatusTypes.DELIVERED),
	OZON_PARTIAL_DELIVERED(108, "PartialDelivered", CrmTypes.OZON, "Доставлен частично", OrderStatusTypes.DELIVERED),
	OZON_CANCELED(110, "Canceled", CrmTypes.OZON, "Отменён", OrderStatusTypes.CANCELED),
	OZON_NOT_IN_DEMAND(111, "NotInDemand", CrmTypes.OZON, "Не востребован", OrderStatusTypes.REDELIVERY),
	OZON_PROBLEM(112, "Problem", CrmTypes.OZON, "Проблема", OrderStatusTypes.CANCELED),
	OZON_RETURING(113, "Returning", CrmTypes.OZON, "Едет обратно", OrderStatusTypes.REDELIVERY),
	OZON_RETURN_READY_FOR_SENDER(114, "ReturnReadyForSender", CrmTypes.OZON, "Готов к выдаче отправителю", OrderStatusTypes.REDELIVERY),
	OZON_RETURNED_TO_SENDER(115, "ReturnedToSender", CrmTypes.OZON, "Передан отправителю", OrderStatusTypes.REDELIVERY_FINISHED);
		
	private final int id;
	private final String code;
	private final CrmTypes crmTypes;
	private final String annotation;
	private final OrderStatusTypes orderStatusType;

	CarrierStatuses(final int id, final String annotation, final OrderStatusTypes orderStatusType) {
		this.id = id;
        this.code = null;
        this.crmTypes = null;
		this.annotation = annotation;
		this.orderStatusType = orderStatusType;
	}

}
