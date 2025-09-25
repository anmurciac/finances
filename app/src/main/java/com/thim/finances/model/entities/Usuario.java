package com.thim.finances.model.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;


@Entity
@Table(name = "usuarios")
public class Usuario {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"); // Validación robusta para el email

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Cuenta> cuentas = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Categoria> categorias = new ArrayList<>();


    public Usuario() {}

    // Constructor que genera ID automáticamente (fue una peuqeña sugerencia)
    public Usuario(String nombre, String email, String password) {
        this.id = UUID.randomUUID().toString();
        setNombre(nombre);
        setEmail(email);
        setpassword(password);
    }

    // Constructor adicional para especificar el ID
    public Usuario(String id, String nombre, String email, String password) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID no puede estar vacío");
        }
        this.id = id.trim();
        setNombre(nombre);
        setEmail(email);
        setpassword(password);
    }

    // Getters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getpassword() { return password; }
    public List<Cuenta> getCuentas() { return cuentas; }
    public List<Categoria> getCategorias() { return categorias; }

    // Setters con validación
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre no puede estar vacío");
        }
        this.nombre = nombre.trim();
    }

    public void setEmail(String email) {
        if (!esEmailValido(email)) {
            throw new IllegalArgumentException("Email no válido");
        }
        this.email = email.trim().toLowerCase();
    }

    public void setpassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("password debe tener al menos 6 caracteres");
        }
        this.password = password;
    }

    // Métodos
    public void agregarCuenta(Cuenta cuenta) {
        if (cuenta != null) {
            cuentas.add(cuenta);
            cuenta.setUsuario(this);
        }
    }
    public void removerCuenta(Cuenta cuenta) {
        cuentas.remove(cuenta);
        cuenta.setUsuario(null);
    }

    public void agregarCategoria(Categoria categoria) {
        if (categoria != null) {
            categorias.add(categoria);
            categoria.setUsuario(this);
        }
    }
    public void removerCategoria(Categoria categoria) {
        categorias.remove(categoria);
        categoria.setUsuario(null);
    }

    // Métodos para buscar cuenta por nombre
    public Cuenta buscarCuentaPorNombre(String nombreCuenta) {
        return cuentas.stream()
                .filter(cuenta -> cuenta.getNombre().equalsIgnoreCase(nombreCuenta))
                .findFirst()
                .orElse(null);
    }

    // Metodo para validar el email (puede servir)
    private boolean esEmailValido(String email) {
        return email != null &&
                !email.trim().isEmpty() &&
                EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    // equals() y hashCode() (otra pequeña sugerencia)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", cuentas=" + cuentas.size() +
                '}';
    }
}