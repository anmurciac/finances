package com.thim.finances.model.entities;

public enum TipoTransaccion {
    INGRESO("INGRESO"),
    GASTO("GASTO");

    private final String valor;

    TipoTransaccion(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}