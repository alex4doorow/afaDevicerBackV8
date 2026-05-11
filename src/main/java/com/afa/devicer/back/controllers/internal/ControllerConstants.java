package com.afa.devicer.back.controllers.internal;

@SuppressWarnings({"PMD.DataClass"})
public final class ControllerConstants {

    public static final String ACTUATOR = "/actuator";
    public static final String API_DOCS = "/v3/api-docs";
    public static final String SWAGGER = "/swagger-ui";

    public static final String BASE_API = "/api/v8";

    public static final String INTEGRATIONS_CDEK = BASE_API + "/integrations/cdek";
    public static final String INTEGRATIONS_SUPPLIERS = BASE_API + "/integrations/suppliers";
    public static final String INTEGRATIONS_UNION = BASE_API + "/integrations/union";

    public static final String DICTIONARIES = BASE_API + "/dictionaries";
    public static final String PRODUCTS = BASE_API + "/products";
    public static final String EMPLOYEES = BASE_API + "/employees";
    public static final String PERSONS = BASE_API + "/persons";
    public static final String CUSTOMERS = BASE_API + "/customers";
    public static final String ORDERS = BASE_API + "/orders";
    public static final String DELIVERY = BASE_API + "/delivery";

    public static final String FILES = BASE_API + "/files";

    /**
     * админ
     */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    /**
     * покупатель
     */
    public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
}
