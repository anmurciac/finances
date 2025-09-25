package com.thim.finances.dtos;

import java.math.BigDecimal;

public class CuentaDTO {
    private String name;
    private String id;
    private BigDecimal saldo;

    public CuentaDTO() {}

    public CuentaDTO(String name, String id, BigDecimal saldo) {
        this.name = name;
        this.id = id;
        this.saldo = saldo;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }


}
