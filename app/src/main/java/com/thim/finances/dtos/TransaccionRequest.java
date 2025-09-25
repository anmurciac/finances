package com.thim.finances.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransaccionRequest {
    private String cuentaId;
    private BigDecimal monto;
    private String descripcion;
    private String categoriaId;
    private LocalDateTime fecha;

    public String getCuentaId() {
        return cuentaId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public String getCategoriaId() {
        return categoriaId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
