package com.afa.devicer.back.enums;

import lombok.Getter;

@Getter
@SuppressWarnings("PMD.ExcessivePublicCount")
public enum DevicerErrors {
    UNKNOWN_ERROR("Неизвестная ошибка"),
    DB_FIELD_SORT_NOT_FOUND("Поле, указанное в сортировке <{0}> не существует"),
    DB_FIELD_ASC_DESC_SORT_NOT_FOUND("Направление сортировки <{0}> не существует. Выбирайте два варианта: asc|desc"),
    DB_FIELD_VALUE_NULL("Значение поля <{0}.{1}> не может быть равно null"),
    DB_FIELD_VALUE_NOT_NULL("Значение поля <{0}.{1}> для новой сущности должно быть равно null, но в запросе пришло значение <{2}>"),
    DB_FIELD_VALUE_NOT_UNIQUE("Значение поля <{0}.{1}> неуникально <{2}>"),
    DB_FIELD_VALUE_SLAVE_DIFFERENT_MASTER("Значение поля <{0}.{1}> в дочерней сущности <{2}> не совпадает со значением в родительской сущности <{3}>"),
    DB_FIELD_VALUE_NOT_NUMBER("Значение поля <{0}.{1}> не число <{2}>"),
    DB_ENTITY_NOT_FOUND("Сущность <{0}> с ключом <{1}> не найдена в БД"),
    MANAGER_UUID_EMPTY("<{0}> UUID для <{1}> пусто"),
    KEYCLOAK_UUID_EMPTY("Keycloak UUID пуст"),
    KEYCLOAK_UUID_EXISTS_ALREADY("Keycloak UUID <{0}> уже существует"),
    KEYCLOAK_UUID_UNKNOWN("Keycloak UUID <{0}> не найден в БД"),
    KEYCLOAK_PRINCIPAL_IS_NULL("Keycloak принципал равен null"),
    KEYCLOAK_PRINCIPAL_INVALID("Keycloak принципал имеет некорректное значение <{0}>"),

    WRONG_USER("Пользователь сервиса с Keycloak UUID <{0}> не найден в БД"),
    USER_IS_DEACTIVATED("Пользователь <{0}> деактивирован, доступ запрещен"),
    USER_MUST_BE_EMPLOYEE("Пользователь должен быть сотрудником компании для данной операции"),
    USER_EMPLOYEE_INVALID("Для данного сотрудника операция запрещена"),

    PERSON_CONTACT_PHONE_IS_INVALID("Значение номера телефона введено некорректно: <{0}>"),
    PERSON_CONTACT_EMAIL_IS_INVALID("Значение email введено некорректно: <{0}>"),


    S3_ERROR("Сервис S3, {0}, ошибка {1}")
    ;

    private final String errorMessage;

    DevicerErrors(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
