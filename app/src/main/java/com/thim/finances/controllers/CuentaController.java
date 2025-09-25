package com.thim.finances.controllers;

import com.thim.finances.dtos.CuentaDTO;
import com.thim.finances.model.entities.Cuenta;
import com.thim.finances.model.services.GestorCuentas;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    private final GestorCuentas gestorCuentas;

    public CuentaController(GestorCuentas gestorCuentas) {
        this.gestorCuentas = gestorCuentas;
    }

    //TODO: Depurar y luego quitar impresiones a consola
    @PostMapping
    public ResponseEntity<CuentaDTO> crearCuenta(
            Authentication authentication,
            @RequestBody CuentaDTO cuentaDTO
    ) {
        String usuarioId = authentication.getName();
        System.out.println("Creando cuenta para usuario: " + usuarioId + ", nombre: " + cuentaDTO.getName() + ", con saldo: " + cuentaDTO.getSaldo());
        try {
            Cuenta cuenta = gestorCuentas.crearCuentaParaUsuario(usuarioId, cuentaDTO.getName(), cuentaDTO.getSaldo());
            System.out.println("Cuenta creada exitosamente: " + cuenta.getId());
            CuentaDTO dto = toCuentaDTO(cuenta);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            System.out.println("Error al crear cuenta: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    //TODO: Revisar
    // Agregar editarCuenta
    @GetMapping
    public ResponseEntity<List<CuentaDTO>> obtenerCuentas(Authentication authentication) {
        String usuarioId = authentication.getName();
        try {
            List<CuentaDTO> cuentas = gestorCuentas.obtenerCuentasDeUsuario(usuarioId);
            return cuentas.isEmpty() ?
                    ResponseEntity.status(204).body(Collections.emptyList()) :
                    ResponseEntity.ok(cuentas);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(404).body(Collections.emptyList());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCuenta(
            @PathVariable String id,
            Authentication authentication
    ) {
        String usuarioId = authentication.getName();
        try {
            gestorCuentas.eliminarCuenta(usuarioId, id);
            return  ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private CuentaDTO toCuentaDTO(Cuenta cuenta) {
        CuentaDTO dto = new CuentaDTO();
        dto.setId(cuenta.getId());
        dto.setName(cuenta.getNombre());
        dto.setSaldo(cuenta.getSaldo());
        return dto;
    }
}
