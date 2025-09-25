package com.thim.finances.model.services;

import jakarta.transaction.Transactional;
import com.thim.finances.model.entities.Usuario;
import com.thim.finances.exceptions.UsuarioNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.thim.finances.repositories.UsuarioRepository;

@Service
public class GestorUsuarios {

    //TODO: Agregar opciones de configuracion de la aplicacion, para cada usuario.
    private final UsuarioRepository usuarioRepository;

    private GestorCategorias gestorCategorias;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public GestorUsuarios(UsuarioRepository usuarioRepository, GestorCategorias gestorCategorias, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.gestorCategorias = gestorCategorias;
        this.passwordEncoder = passwordEncoder;
    }
    public Usuario registrarUsuario(String nombre, String email, String password) {
        validarDatosUsuario(nombre, email, password);

        if(usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + email);
        }

        Usuario nuevoUsuario = new Usuario(nombre, email, passwordEncoder.encode(password));
        Usuario u = usuarioRepository.save(nuevoUsuario);
        gestorCategorias.crearCategoriasPredeterminadasParaUsuario(u.getId());
        return u;
    }

    // Elimine la opcion de crear con id especifico debido a que podria causar problemas en la base de datos

    public Usuario buscarUsuario(String id) throws UsuarioNoEncontradoException {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
    }

    public Usuario buscarUsuarioPorEmail(String email) throws UsuarioNoEncontradoException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con el email: " + email));
    }

    public boolean autenticarUsuario(String id, String password) {
        try {
            Usuario usuario = buscarUsuario(id);
            return passwordEncoder.matches(password, usuario.getpassword());
        } catch (UsuarioNoEncontradoException e) {
            return false;
        }
    }

    public boolean autenticarUsuarioPorEmail(String email, String password) {
        try {
            Usuario usuario = buscarUsuarioPorEmail(email);
            return passwordEncoder.matches(password, usuario.getpassword());
        } catch (UsuarioNoEncontradoException e) {
            return false;
        }
    }

    public boolean existeUsuario(String id) {
        return usuarioRepository.existsById(id);
    }

    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Transactional
    public void eliminarUsuario(String id) throws UsuarioNoEncontradoException {
        if(!usuarioRepository.existsById(id)) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    public int getCantidadUsuarios() {
        return (int) usuarioRepository.count();
    }

    // Métodos privado para validar datos del usuario
    private void validarDatosUsuario(String nombre, String email, String password) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("La password debe tener al menos 6 caracteres");
        }
    }
}