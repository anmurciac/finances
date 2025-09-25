package com.thim.finances.dtos;

import java.math.BigDecimal;

public class BalanceResponse {
    private BigDecimal ingresos;
    private BigDecimal gastos;
    private BigDecimal balance;

    //TODO: Cambiar esta parte, se me olvidaba este placeholder
    public BalanceResponse(BigDecimal ingresos, BigDecimal gastos) {
        this.ingresos = ingresos;
        this.gastos = gastos;
        this.balance = ingresos.subtract(gastos);
    }

    public BigDecimal getIngresos() {
        return ingresos;
    }

    public BigDecimal getGastos() {
        return gastos;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
