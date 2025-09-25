package com.thim.finances.model.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("GASTO")
public class Gasto extends Transaccion {

    public Gasto() {}
    public Gasto(BigDecimal monto, String descripcion, Categoria categoria, Cuenta cuenta, LocalDateTime fecha) {
        super(monto, descripcion, categoria, cuenta, fecha);
        this.tipo = getTipo();
        // Validaciones específicas de Gasto
        if (categoria.getTipo() != TipoCategoria.GASTO) {
            throw new IllegalArgumentException("La categoría debe ser de tipo GASTO");
        }
    }

    @Override
    public TipoTransaccion getTipo() {
        return TipoTransaccion.GASTO;
    }

}