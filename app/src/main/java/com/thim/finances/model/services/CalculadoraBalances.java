package com.thim.finances.model.services;

import com.thim.finances.dtos.BalanceResponse;
import com.thim.finances.model.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.thim.finances.repositories.TransaccionRepository;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CalculadoraBalances {

    private final TransaccionRepository transaccionRepository;

    @Autowired
    public CalculadoraBalances(TransaccionRepository transaccionRepository) {
        this.transaccionRepository = transaccionRepository;
    }

    // Métodos para calcular balances
    public BigDecimal calcularBalanceCuenta(Cuenta cuenta) {
        Objects.requireNonNull(cuenta, "La cuenta no puede ser nula");
        return cuenta.getSaldo();
    }

    public BigDecimal calcularBalanceTotalUsuario(Usuario usuario) {
        Objects.requireNonNull(usuario, "El usuario no puede ser nulo");

        return usuario.getCuentas().stream()
                .map(Cuenta::getSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularBalancePorCategoria(Usuario usuario, String nombreCategoria) {
        Objects.requireNonNull(usuario, "El usuario no puede ser nulo");
        Objects.requireNonNull(nombreCategoria, "El nombre de categoría no puede ser nulo");

        return usuario.getCuentas().stream()
                .flatMap(cuenta -> cuenta.getTransacciones().stream())
                .filter(transaccion ->
                        transaccion.getCategoria().getNombre().equalsIgnoreCase(nombreCategoria))
                .map(transaccion -> {
                    if (transaccion instanceof Ingreso) {
                        return transaccion.getMonto();
                    } else {
                        return transaccion.getMonto().negate();
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularBalancePorCategoria(String usuarioId, String categoriaId) {
        List<Transaccion> transacciones = transaccionRepository.findByUsuarioIdAndCategoriaId(usuarioId, categoriaId);

        return transacciones.stream()
                .map(transaccion -> {
                    if (transaccion.getTipo() == TipoTransaccion.INGRESO) {
                        return transaccion.getMonto();
                    } else {
                        return transaccion.getMonto().negate();
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularIngresosTotal(String usuarioId) {
        return transaccionRepository.sumIngresosByUsuario(usuarioId);
    }

    public BigDecimal calcularGastosTotal(String usuarioId) {
        return transaccionRepository.sumGastosByUsuario(usuarioId);
    }


//    public List<BalanceResponse> calcularBalancesMensuales(String usuarioId) {
//        List<YearMonth> yearMonths = transaccionRepository.findDistinctYearMonthsByUsuarioId(usuarioId);
//        return yearMonths.stream()
//                .map(ym -> {
//                    int year = ym.getYear();
//                    int month = ym.getMonthValue();
//                    BigDecimal ingresos = transaccionRepository.sumIngresosByUsuarioAndPeriodo(usuarioId, year, month);
//                    BigDecimal gastos = transaccionRepository.sumGastosByUsuarioAndPeriodo(usuarioId, year, month);
//                    return new BalanceResponse(year, month, ingresos, gastos);
//                })
//                .collect(Collectors.toList());
//    }
}