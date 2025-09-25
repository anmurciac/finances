package com.thim.finances.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransaccionDTO {
    private String tipo;
    private BigDecimal monto;
    private String descripcion;

    private String categoria;
    private LocalDateTime fecha;
    private String id;

    public TransaccionDTO(String tipo, BigDecimal monto, String descripcion, String categoria, LocalDateTime fecha, String id) {
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.fecha = fecha;
        this.id = id;
    }

    public TransaccionDTO() {}

    // Getters
    public String getId() {
        return id;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getTipo() {
        return tipo;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
