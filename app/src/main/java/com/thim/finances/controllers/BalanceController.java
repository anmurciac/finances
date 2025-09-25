package com.thim.finances.controllers;

import com.thim.finances.dtos.BalanceResponse;
import com.thim.finances.model.services.CalculadoraBalances;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/balances")
public class BalanceController {

    private final CalculadoraBalances calculadoraBalances;

    public BalanceController(CalculadoraBalances calculadoraBalances) {
        this.calculadoraBalances = calculadoraBalances;
    }

    //TODO: Como prueba de concepto funciona bien, pero claramente no entrega bien los datos.
    @GetMapping("/mensual")
    public ResponseEntity<BalanceResponse> getBalanceMensual(
            Authentication authentication,
            @RequestParam int year,
            @RequestParam int month
    ) {
        String usuarioId = authentication.getName();

        try {
            BigDecimal ingresos = calculadoraBalances.calcularIngresosTotal(usuarioId);
            BigDecimal gastos = calculadoraBalances.calcularGastosTotal(usuarioId);
            return ResponseEntity.ok(new BalanceResponse(ingresos, gastos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
