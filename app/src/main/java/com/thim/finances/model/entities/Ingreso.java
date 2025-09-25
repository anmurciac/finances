package com.thim.finances.model.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("INGRESO")
public class Ingreso extends Transaccion {
    public  Ingreso() {}
    public Ingreso(BigDecimal monto, String descripcion, Categoria categoria, Cuenta cuenta, LocalDateTime fecha) {
        super(monto, descripcion, categoria, cuenta, fecha);
        this.tipo = getTipo();
        // Validación
        if (categoria.getTipo() != TipoCategoria.INGRESO) {
            throw new IllegalArgumentException("Categoría debe ser de tipo INGRESO");
        }
    }
    @Override
    public TipoTransaccion getTipo() {
        return TipoTransaccion.INGRESO;
    }
}