package com.thim.finances.model.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(name = "transacciones")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
public abstract class Transaccion {
   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   private String id;
   @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;
   @Column(nullable = false)
    private LocalDateTime fecha;
   @Column(nullable = false, length = 255)
    private String descripcion;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "cuenta_id", nullable = false)
   private Cuenta cuenta; //Agrego esta propiedad siendo que toda transaccion debe pertenecer a una cuenta

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_transaccion")
    TipoTransaccion tipo;

    //Constructor para la base de datos
    protected Transaccion() {}
    public Transaccion(
            BigDecimal monto,
            String descripcion,
            Categoria categoria,
            Cuenta cuenta,
            LocalDateTime fecha
    )
    {
        this.id = UUID.randomUUID().toString();
        setMonto(monto);
        setDescripcion(descripcion);
        setCategoria(categoria);
        setCuenta(cuenta);
        this.fecha = fecha;
    }

    // Getters
    public String getId() { return id; }
    public BigDecimal getMonto() { return monto; }
    public LocalDateTime getFecha() { return fecha; }
    public String getDescripcion() { return descripcion; }
    public Categoria getCategoria() { return categoria; }
    public Cuenta getCuenta() {
        return cuenta;
    }

    // Setters con validación
    public void setMonto(BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo");
        }
        this.monto = monto;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser nula");
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía");
        }
        this.descripcion = descripcion.trim();
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = Objects.requireNonNull(categoria, "La categoría no puede ser nula");
    }
    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public abstract TipoTransaccion getTipo();
}