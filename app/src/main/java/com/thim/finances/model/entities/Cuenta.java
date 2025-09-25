package com.thim.finances.model.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "cuentas")
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal saldo;

    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaccion> transacciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; //De manera similar a transaccion, cada cuenta debe tener un usuario

    public Cuenta() {}
    public Cuenta(String nombre, BigDecimal saldo) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la cuenta no puede estar vacío");
        }
        this.nombre = nombre.trim();
        this.saldo = saldo;
        this.transacciones = new ArrayList<>();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public List<Transaccion> getTransacciones() {
        return Collections.unmodifiableList(transacciones);
    }

    public Usuario getUsuario() {
        return usuario;
    }

    // Setters con validación
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la cuenta no puede estar vacío");
        }
        this.nombre = nombre.trim();
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    // Métodos de operaciones
    public void agregarTransaccion(Transaccion transaccion) {
        Objects.requireNonNull(transaccion, "La transacción no puede ser nula");
        transaccion.setCuenta(this);
        this.transacciones.add(transaccion);
        aplicarTransaccion(transaccion);
    }

    public void removerTransaccion(Transaccion transaccion) {
        if (transacciones.remove(transaccion)) {
            revertirTransaccion(transaccion);
        }
    }

    public void actualizarSaldo(BigDecimal nuevoSaldo) {
        this.saldo = Objects.requireNonNull(nuevoSaldo, "El saldo no puede ser nulo");
    }

    public void ajustarSaldo(BigDecimal ajuste) {
        Objects.requireNonNull(ajuste, "El ajuste no puede ser nulo");
        this.saldo = this.saldo.add(ajuste);
    }

    // Métodos privados
    private void aplicarTransaccion(Transaccion transaccion) {
        if (transaccion instanceof Ingreso) {
            this.saldo = saldo.add(transaccion.getMonto());
        } else {
            this.saldo = saldo.subtract(transaccion.getMonto());
        }
    }

    private void revertirTransaccion(Transaccion transaccion) {
        if (transaccion instanceof Ingreso) {
            this.saldo = saldo.subtract(transaccion.getMonto());
        } else {
            this.saldo = saldo.add(transaccion.getMonto());
        }
    }
    // Transacciones ordenasdas por fecha
    public List<Transaccion> getTransaccionesOrdenadasAsc() {
        return transacciones.stream().sorted(Comparator.comparing(Transaccion::getFecha)).collect(Collectors.toList());
    }

    public List<Transaccion> getTransaccionesOrdenadasDesc() {
        return transacciones.stream().sorted(Comparator.comparing(Transaccion::getFecha).reversed()).collect(Collectors.toList());
    }


}