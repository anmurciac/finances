package com.thim.finances.model.services;

import com.thim.finances.exceptions.UsuarioNoEncontradoException;
import com.thim.finances.model.entities.Usuario;
import com.thim.finances.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import com.thim.finances.model.entities.Categoria;
import com.thim.finances.model.entities.TipoCategoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.thim.finances.repositories.CategoriaRepository;

import java.util.List;
import java.util.Objects;

//TODO: Filtrar por tipo
@Service
public class GestorCategorias {
    private final CategoriaRepository categoriaRepository;

    private final UsuarioRepository usuarioRepository;
    @Autowired
    public GestorCategorias(CategoriaRepository categoriaRepository, UsuarioRepository usuarioRepository) {
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void crearCategoriasPredeterminadasParaUsuario(String usuarioId) {
        agregarCategoriaPorDefecto(usuarioId, "Salario", TipoCategoria.INGRESO);
        agregarCategoriaPorDefecto(usuarioId, "Inversiones", TipoCategoria.INGRESO);
        agregarCategoriaPorDefecto(usuarioId, "Freelance", TipoCategoria.INGRESO);
        agregarCategoriaPorDefecto(usuarioId, "Bonos", TipoCategoria.INGRESO);

        agregarCategoriaPorDefecto(usuarioId, "Comida", TipoCategoria.GASTO);
        agregarCategoriaPorDefecto(usuarioId, "Transporte", TipoCategoria.GASTO);
        agregarCategoriaPorDefecto(usuarioId, "Entretenimiento", TipoCategoria.GASTO);
        agregarCategoriaPorDefecto(usuarioId, "Servicios", TipoCategoria.GASTO);
        agregarCategoriaPorDefecto(usuarioId, "Compras", TipoCategoria.GASTO);
    }
    @Transactional
    private void agregarCategoriaPorDefecto(String usuarioId, String nombre, TipoCategoria tipo) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de categoría no puede ser nulo");
        }

        Usuario usuario;
        try {
            usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        } catch (UsuarioNoEncontradoException e) {
            throw new RuntimeException(e);
        }

        // Verificar si la categoría ya existe para este usuario
        if (categoriaRepository.existsByNombreAndUsuario(nombre, usuario)) {
            return;
        }

        Categoria nuevaCategoria = new Categoria(nombre, tipo);
        nuevaCategoria.setUsuario(usuario);
        usuario.agregarCategoria(nuevaCategoria);
        categoriaRepository.save(nuevaCategoria);
    }

    @Transactional
    public Categoria agregarCategoriaParaUsuario(
            String usuarioId, String nombre, TipoCategoria tipo) throws UsuarioNoEncontradoException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de categoría no puede ser nulo");
        }

        if (categoriaRepository.existsByNombre(nombre)) {
            throw new IllegalArgumentException("Categoría ya existe: " + nombre);
        }

        Categoria nuevaCategoria = new Categoria(nombre, tipo);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));


        nuevaCategoria.setUsuario(usuario);
        usuario.agregarCategoria(nuevaCategoria);

        return categoriaRepository.save(nuevaCategoria);
    }

    public List<Categoria> obtenerCategoriasPorTipo(TipoCategoria tipo) {
        Objects.requireNonNull(tipo, "El tipo no puede ser nulo");
        return categoriaRepository.findByTipo(tipo);
    }

    public List<Categoria> obtenerTodasLasCategorias() {
        return categoriaRepository.findAll();
    }

    public Categoria buscarCategoria(String nombre) {
        return categoriaRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + nombre));
    }

    public Categoria buscarCategoriaPorId(String id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + id));
    }

    public boolean existeCategoria(String nombre) {
       return categoriaRepository.existsByNombre(nombre);
    }

    //TODO: Agregar una excepcion para mantener la coherencia con el resto del código (?).
    // De manera similar a las transacciones, agregar la posibilidad de cambiar el tipo
    @Transactional
    public Categoria editarCategoria(String categoriaId, String nuevoNombre ) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow();

        categoria.setNombre(nuevoNombre);
        categoriaRepository.save(categoria);
        return  categoria;
    }
    @Transactional
    public void eliminarCategoria(String usuarioId, String id) {
        Categoria categoria = buscarCategoriaPorId(id);
        categoriaRepository.delete(categoria);
    }
    public int getCantidadCategorias() {
        return (int) categoriaRepository.count();
    }
    public List<Categoria> obtenerCategoriasDeUsuario(String usuarioId) throws UsuarioNoEncontradoException {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        return usuario.getCategorias();
    }
}