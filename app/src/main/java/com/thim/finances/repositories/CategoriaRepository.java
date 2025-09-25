package com.thim.finances.repositories;

import com.thim.finances.model.entities.Categoria;
import com.thim.finances.model.entities.TipoCategoria;
import com.thim.finances.model.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, String> {
    List<Categoria> findByTipo(TipoCategoria tipo);
    boolean existsByNombre(String nombre);
    Optional<Categoria> findByNombre(String nombre);

    List<Categoria> findByUsuarioId(String usuarioId);

    boolean existsByNombreAndUsuario(String nombre, Usuario usuario);
}