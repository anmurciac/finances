package com.thim.finances.repositories;

import com.thim.finances.dtos.CuentaDTO;
import com.thim.finances.model.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    @Query("SELECT new com.thim.finances.dtos.CuentaDTO(c.nombre, c.id, c.saldo) " +
            "FROM Usuario u JOIN u.cuentas c WHERE u.id = :usuarioId")
    List<CuentaDTO> findCuentasByUsuarioId(@Param("usuarioId") String usuarioId);
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}