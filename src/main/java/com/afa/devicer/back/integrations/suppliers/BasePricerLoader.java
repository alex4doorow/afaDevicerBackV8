package com.afa.devicer.back.integrations.suppliers;

public abstract class BasePricerLoader<CONNECTOR> {

    /**
     * Интеграция, коннектор к внешнему API получение прайса и загрузка к нам (файл, фид)
     */
    protected final CONNECTOR connector;

    protected BasePricerLoader(final CONNECTOR connector) {
        this.connector = connector;
    }
}