package com.afa.devicer.back.enums;

import lombok.Getter;

@Getter
@SuppressWarnings({"PMD.UnnecessaryFullyQualifiedName", "PMD.AvoidDuplicateLiterals"})
public enum DeliveryTypes {

    UNKNOWN(0, "Неопределено", "неопределено"),
    CDEK_PVZ_TYPICAL(101, "CDEK", "СДЭК, ПВЗ"),
    CDEK_PVZ_ECONOMY(102, "CDEK", "СДЭК, ЭКОНОМ ПВЗ"),
    CDEK_COURIER(103, "CDEK", "СДЭК, курьер"),
    CDEK_COURIER_ECONOMY(104, "CDEK", "СДЭК, ЭКОНОМ курьер"),
    DELLIN(201, "ТК Деловые Линии", "Деловые Линии"),
    COURIER_MOSCOW_TYPICAL(301, "МОСКВА курьер 1-2 дня", "курьер, 1-2 дня"),
    COURIER_MOSCOW_FAST(302, "МОСКВА курьер, сегодня", "курьер, сегодня"),
    COURIER_MO_TYPICAL(303, "МО курьер, 1-2 дня", "МО, курьер"),
    POST_TYPICAL(401, "ПОЧТА РОССИИ", "ПОЧТА, обычное"),
    POST_I_CLASS(402, "ПОЧТА РОССИИ", "ПОЧТА, 1-й класс"),
    POST_EMS(405, "ПОЧТА EMS", "ПОЧТА, EMS"),
    PICKUP(403, "самовывоз, сегодня", "самовывоз"),
    YANDEX_MARKET_FBS(501, "YANDEX MARKET", "до склада Яндекс-Маркет"),
    OZON_FBS(701, "OZON", "до склада OZON"),
    YANDEX_GO(601, "YANDEX GO", "Яндекс-Логистика"),
    OZON_ROCKET_PICKPOINT(801, "OZON Rocket", "OZON Rocket ПВЗ", "PickPoint"),
    OZON_ROCKET_POSTAMAT(802, "OZON Rocket", "OZON Rocket постамат", "Postamat"),
    OZON_ROCKET_COURIER(803, "OZON Rocket", "OZON Rocket курьер", "Courier");

    private final int id;
    private final String category;
    private final String annotation;
    private final String code;

    DeliveryTypes(final int id, final String category, final String annotation) {
        this.id = id;
        this.category = category;
        this.annotation = annotation;
        this.code = null;
    }

    DeliveryTypes(final int id, final String category, final String annotation, final String code) {
        this.id = id;
        this.category = category;
        this.annotation = annotation;
        this.code = code;
    }

}
