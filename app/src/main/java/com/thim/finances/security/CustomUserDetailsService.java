package com.thim.finances.security;

import com.thim.finances.exceptions.UsuarioNoEncontradoException;
import com.thim.finances.model.entities.Usuario;
import com.thim.finances.model.services.GestorUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final GestorUsuarios gestorUsuarios;

    @Autowired
    public CustomUserDetailsService(GestorUsuarios gestorUsuarios) {
        this.gestorUsuarios = gestorUsuarios;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            Usuario usuario = gestorUsuarios.buscarUsuarioPorEmail(email);
            // Crea un UserDetails con el email como username y la contraseña hasheada
            return new org.springframework.security.core.userdetails.User(
                    usuario.getId(),
                    usuario.getpassword(),
                    new ArrayList<>() // Lista de authorities/roles (vacía por ahora)
            );
        } catch (UsuarioNoEncontradoException e) {
            throw new UsernameNotFoundException("Usuario no encontrado con email: " + email, e);
        }
    }
}
