package com.thim.finances.model.services;

import com.thim.finances.dtos.CuentaDTO;
import com.thim.finances.exceptions.CuentaInvalidaException;
import com.thim.finances.exceptions.UsuarioNoEncontradoException;
import jakarta.transaction.Transactional;
import com.thim.finances.model.entities.Cuenta;
import com.thim.finances.model.entities.Usuario;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.thim.finances.repositories.CuentaRepository;
import com.thim.finances.repositories.UsuarioRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GestorCuentas {

    private final CuentaRepository cuentaRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public GestorCuentas(CuentaRepository cuentaRepository, UsuarioRepository usuarioRepository) {
        this.cuentaRepository = cuentaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Cuenta crearCuentaParaUsuario(String usuarioId, String nombreCuenta, BigDecimal saldo)
            throws CuentaInvalidaException, UsuarioNoEncontradoException {

        validarNombreCuenta(nombreCuenta);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        Cuenta nuevaCuenta = new Cuenta(nombreCuenta, saldo);
        nuevaCuenta.setUsuario(usuario);
        usuario.agregarCuenta(nuevaCuenta);

        return cuentaRepository.save(nuevaCuenta);
    }

    public Cuenta buscarCuenta(String usuarioId, String cuentaId)
            throws UsuarioNoEncontradoException, CuentaInvalidaException {

        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaInvalidaException("Cuenta no encontrada con ID: " + cuentaId));

        if (!cuenta.getUsuario().getId().equals(usuarioId)) {
            throw new CuentaInvalidaException("La cuenta no pertenece al usuario");
        }

        return cuenta;
    }

    @Transactional
    public void eliminarCuenta(String usuarioId, String cuentaId)
            throws UsuarioNoEncontradoException, CuentaInvalidaException {

        Cuenta cuenta = buscarCuenta(usuarioId, cuentaId);
        cuentaRepository.delete(cuenta);

        // Actualizar relación en el usuario
        Usuario usuario = cuenta.getUsuario();
        usuario.removerCuenta(cuenta);
    }

    @Transactional
    public void renombrarCuenta(String usuarioId, String cuentaId, String nuevoNombre)
            throws UsuarioNoEncontradoException, CuentaInvalidaException {

        validarNombreCuenta(nuevoNombre);
        Cuenta cuenta = buscarCuenta(usuarioId, cuentaId);
        cuenta.setNombre(nuevoNombre);
        cuentaRepository.save(cuenta);
    }

    // Métodos de validación privados
    private void validarNombreCuenta(String nombreCuenta) throws CuentaInvalidaException {
        if (nombreCuenta == null || nombreCuenta.trim().isEmpty()) {
            throw new CuentaInvalidaException("El nombre de la cuenta no puede estar vacío");
        }
        if (nombreCuenta.length() > 50) {
            throw new CuentaInvalidaException("El nombre de la cuenta es demasiado largo");
        }
    }

    //Se hizo necesaria para el controlador de las cuentas en la base de datos
    @Transactional
    public List<CuentaDTO> obtenerCuentasDeUsuario(String usuarioId) {
        return usuarioRepository.findCuentasByUsuarioId(usuarioId);
    }
}