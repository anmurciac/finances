package com.thim.finances.controllers;

import com.thim.finances.dtos.TransaccionDTO;
import com.thim.finances.dtos.TransaccionRequest;
import com.thim.finances.exceptions.SaldoInsuficienteException;
import com.thim.finances.exceptions.TransaccionInvalidaException;
import com.thim.finances.model.entities.Transaccion;
import com.thim.finances.model.services.GestorTransacciones;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    private final GestorTransacciones gestorTransacciones;

    public TransaccionController(GestorTransacciones gestorTransacciones) {
        this.gestorTransacciones = gestorTransacciones;
    }


    @GetMapping
    public ResponseEntity<List<TransaccionDTO>> obtenerTransacciones(
            @RequestParam(value = "cuenta") String cuentaId,
            Authentication authentication
    ) {
        try {
            List<TransaccionDTO> transacciones = gestorTransacciones.obtenerTransaccionesDeCuenta(cuentaId);
            return transacciones.isEmpty() ?
                    ResponseEntity.status(204).body(Collections.emptyList()) :
                    ResponseEntity.ok(transacciones);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(404).body(Collections.emptyList());
        }
    }
    @PostMapping("/ingresos")
    public ResponseEntity<TransaccionDTO> registrarIngreso(
            @RequestBody TransaccionRequest request,
            Authentication authentication
            ) {
        try {
            Transaccion transaccion = gestorTransacciones.registrarIngreso(
                    request.getCuentaId(),
                    request.getMonto(),
                    request.getDescripcion(),
                    request.getCategoriaId(),
                    request.getFecha()
            );
            return ResponseEntity.ok(toTransaccionDTO(transaccion));
        } catch (TransaccionInvalidaException | SaldoInsuficienteException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/gastos")
    public  ResponseEntity<TransaccionDTO> registrarGasto(
            @RequestBody TransaccionRequest request,
            Authentication authentication
    ) {
        try {
            Transaccion transaccion = gestorTransacciones.registrarGasto(
                    request.getCuentaId(),
                    request.getMonto(),
                    request.getDescripcion(),
                    request.getCategoriaId(),
                    request.getFecha()
            );
            return ResponseEntity.ok(toTransaccionDTO(transaccion));
        } catch (TransaccionInvalidaException | SaldoInsuficienteException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<TransaccionDTO> editarTransaccion(
            @PathVariable String id,
            @RequestBody TransaccionRequest request,
            Authentication authentication) {
        try {
            Transaccion transaccion = gestorTransacciones.editarTransaccion(
                    request.getCuentaId(),
                    id,
                    request.getMonto(),
                    request.getDescripcion(),
                    request.getFecha(),
                    request.getCategoriaId()
            );
            return ResponseEntity.ok(toTransaccionDTO(transaccion));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //TODO: Terminar la operacion en el servicio y añadir en el controlador
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTransaccion(
            @PathVariable String id,
            Authentication authentication
    ) {
        String usuarioId = authentication.getName();
        try {
            gestorTransacciones.eliminarTransaccion(usuarioId, id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }


    private TransaccionDTO toTransaccionDTO(Transaccion transaccion) {
        TransaccionDTO dto = new TransaccionDTO();
        dto.setId(transaccion.getId());
        dto.setMonto(transaccion.getMonto());
        dto.setDescripcion(transaccion.getDescripcion());
        dto.setCategoria(transaccion.getCategoria().getNombre());
        dto.setFecha(transaccion.getFecha());
        dto.setTipo(transaccion.getTipo().name());
        return dto;
    }
}
