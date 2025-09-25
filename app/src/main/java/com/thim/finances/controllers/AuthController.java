package com.thim.finances.controllers;

import com.thim.finances.dtos.AuthResponse;
import com.thim.finances.dtos.LoginRequest;
import com.thim.finances.model.entities.Usuario;
import com.thim.finances.model.services.GestorUsuarios;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.thim.finances.security.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    //TODO: Tal vez no vaya aqui, pero falta la operacion de cerrar sesion

    private final GestorUsuarios gestorUsuarios;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public AuthController(GestorUsuarios gestorUsuarios, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.gestorUsuarios = gestorUsuarios;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest request) {
        System.out.println("Intentando registrar usuario: " + request.getPassword());
        try {
           Usuario usuario = gestorUsuarios.registrarUsuario(request.getName(), request.getEmail(), request.getPassword());
           String token = jwtService.generateToken(usuario.getId());
            System.out.println("Usuario registrado exitosamente: " + usuario.getId());
            return ResponseEntity.status(201).body(new AuthResponse(token, usuario.getId()));
        } catch (Exception e) {
            System.out.println("Error durante el registro: " + e.getMessage());
            return ResponseEntity.badRequest().body("{\"error\": \"Error al registrar: " + e.getMessage() + "\"}");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println("Iniciando proceso de login para: " + request.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            Usuario usuario = gestorUsuarios.buscarUsuarioPorEmail(request.getEmail());
            String userId = usuario.getId();
            String token = jwtService.generateToken(userId);
            return ResponseEntity.ok(new AuthResponse(token, userId));
        } catch (Exception e) {
            System.out.println("Error durante la autenticación: " + e.getMessage());
            return ResponseEntity.status(401).body("{\"error\": \"Credenciales inválidas: " + e.getMessage() + "\"}");
        }
    }

}
