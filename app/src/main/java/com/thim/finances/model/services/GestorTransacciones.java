package com.thim.finances.model.services;

import com.thim.finances.dtos.TransaccionDTO;
import com.thim.finances.exceptions.SaldoInsuficienteException;
import com.thim.finances.exceptions.TransaccionInvalidaException;
import com.thim.finances.model.entities.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.thim.finances.repositories.CategoriaRepository;
import com.thim.finances.repositories.CuentaRepository;
import com.thim.finances.repositories.TransaccionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


//TODO: Agregar filtrado por fechas, por categorias y por cuentas
//TODO: Borrar transacciones
@Service
public class GestorTransacciones {
    private final TransaccionRepository transaccionRepository;
    private final CuentaRepository cuentaRepository;
    private final CategoriaRepository categoriaRepository;

    public GestorTransacciones(
            TransaccionRepository transaccionRepository,
            CuentaRepository cuentaRepository,
            CategoriaRepository categoriaRepository,
            CalculadoraBalances calculadora
    ) {
        this.transaccionRepository = transaccionRepository;
        this.cuentaRepository = cuentaRepository;
        this.categoriaRepository = categoriaRepository;
        Objects.requireNonNull(calculadora, "La calculadora de balances no puede ser nula");
    }

    @Transactional
    public Transaccion registrarIngreso(
            String cuentaId, BigDecimal monto, String descripcion, String categoriaId, LocalDateTime fecha
    )
            throws TransaccionInvalidaException, SaldoInsuficienteException {

        Cuenta cuenta = obtenerCuenta(cuentaId);
        Categoria categoria = obtenerCategoria(categoriaId);

        Ingreso ingreso = new Ingreso(monto, descripcion, categoria, cuenta, fecha);
        return procesarTransaccion(cuenta, ingreso);
    }

    @Transactional
    public Transaccion registrarGasto(
            String cuentaId, BigDecimal monto, String descripcion, String categoriaId, LocalDateTime fecha
    )
            throws SaldoInsuficienteException, TransaccionInvalidaException {

        Cuenta cuenta = obtenerCuenta(cuentaId);
        validarSaldoSuficiente(cuenta, monto);
        Categoria categoria = obtenerCategoria(categoriaId);

        Gasto gasto = new Gasto(monto, descripcion, categoria, cuenta, fecha);
        return procesarTransaccion(cuenta, gasto);
    }

    @Transactional
    public Transaccion procesarTransaccion(Cuenta cuenta, Transaccion transaccion)
            throws TransaccionInvalidaException {

        validarParametros(cuenta, transaccion);
        validarCompatibilidadCategoria(transaccion);

        // Actualizar saldo de la cuenta
        if (transaccion.getTipo() == TipoTransaccion.INGRESO) {
            cuenta.setSaldo(cuenta.getSaldo().add(transaccion.getMonto()));
        } else {
            cuenta.setSaldo(cuenta.getSaldo().subtract(transaccion.getMonto()));
        }

        cuenta.agregarTransaccion(transaccion);
        cuentaRepository.save(cuenta);
        return transaccionRepository.save(transaccion);
    }
    @Transactional
    public List<TransaccionDTO> obtenerTransaccionesDeCuenta(String cuentaId) {
        return cuentaRepository.findTransaccionesByCuentaId(cuentaId);
    }

    //TODO: Despues seria bueno agregar la opcion de cambiar de tipo de transaccion
    public Transaccion editarTransaccion(String cuentaId, String transaccionId, BigDecimal nuevoMonto, String nuevaDescripcion, LocalDateTime nuevaFecha, String nuevaCategoriaId) //Siguiendo el request
            throws SaldoInsuficienteException, TransaccionInvalidaException {
        // Recuperar cuenta y transacción existente
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new TransaccionInvalidaException("Cuenta no encontrada"));
        Transaccion transaccion = transaccionRepository.findById(transaccionId)
                .orElseThrow(() -> new TransaccionInvalidaException("Transacción no encontrada"));
        Categoria nuevaCategoria = categoriaRepository.findById(nuevaCategoriaId)
                .orElseThrow(() -> new TransaccionInvalidaException("Categoria no encontrada"));


        // Guardar estado original para reversión
        BigDecimal montoOriginal = transaccion.getMonto();
        TipoTransaccion tipoOriginal = transaccion.getTipo();

        // Revertir transacción original
        if (tipoOriginal == TipoTransaccion.INGRESO) {
            cuenta.setSaldo(cuenta.getSaldo().subtract(montoOriginal));
        } else {
            cuenta.setSaldo(cuenta.getSaldo().add(montoOriginal));
        }

        transaccion.setMonto(nuevoMonto);
        transaccion.setDescripcion(nuevaDescripcion);
        transaccion.setFecha(nuevaFecha);
        transaccion.setCategoria(nuevaCategoria);


        // Validar nuevo estado
        if (tipoOriginal == TipoTransaccion.GASTO) {
            validarSaldoSuficiente(cuenta, nuevoMonto);
        }
        // Aplicar nueva transacción
        if (tipoOriginal == TipoTransaccion.INGRESO) {
            cuenta.setSaldo(cuenta.getSaldo().add(nuevoMonto));
        } else {
            cuenta.setSaldo(cuenta.getSaldo().subtract(nuevoMonto));
        }


        transaccionRepository.save(transaccion);
        cuentaRepository.save(cuenta);
        return transaccion;
    }
    @Transactional
    public void eliminarTransaccion(String usuarioId, String transaccionId) {
        Transaccion transaccion = transaccionRepository.findById(transaccionId)
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada: " + transaccionId));
        Cuenta cuenta = transaccion.getCuenta();
        if (!cuenta.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("Transacción no pertenece al usuario");
        }
        cuenta.removerTransaccion(transaccion);
        cuentaRepository.save(cuenta);
        transaccionRepository.delete(transaccion);
    }


    // Métodos privados

    private Cuenta obtenerCuenta(String cuentaId) {
        return cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));
    }

    private Categoria obtenerCategoria(String categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
    }
    private void validarParametros(Cuenta cuenta, Transaccion transaccion) throws TransaccionInvalidaException {
        if (cuenta == null || transaccion == null) {
            throw new TransaccionInvalidaException("La cuenta y la transacción no pueden ser nulas");
        }

        if (transaccion.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransaccionInvalidaException("El monto debe ser positivo");
        }
        //TODO: Agregar limite para la descripcion
        if (transaccion.getDescripcion() == null || transaccion.getDescripcion().trim().isEmpty()) {
            throw new TransaccionInvalidaException("La descripción no puede estar vacía");
        }

        if (transaccion.getCategoria() == null) {
            throw new TransaccionInvalidaException("La categoría no puede ser nula");
        }
    }

    private void validarCompatibilidadCategoria(Transaccion transaccion) throws TransaccionInvalidaException {
        if (transaccion instanceof Ingreso &&
                transaccion.getCategoria().getTipo() != TipoCategoria.INGRESO) {
            throw new TransaccionInvalidaException("La categoría no es válida para ingresos");
        }

        if (transaccion instanceof Gasto &&
                transaccion.getCategoria().getTipo() != TipoCategoria.GASTO) {
            throw new TransaccionInvalidaException("La categoría no es válida para gastos");
        }
    }

    private void validarSaldoSuficiente(Cuenta cuenta, BigDecimal monto) throws SaldoInsuficienteException {
        BigDecimal saldoActual = cuenta.getSaldo();
        if (saldoActual.compareTo(monto) < 0) {
            throw new SaldoInsuficienteException(
                    String.format("Saldo insuficiente. Disponible: %s, requerido: %s",
                            saldoActual, monto)
            );
        }
    }
    private Transaccion buscarTransaccion(Cuenta cuenta, String transaccionId) {
        return cuenta.getTransacciones().stream()
                .filter(t -> t.getId().equals(transaccionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada"));
    }
}