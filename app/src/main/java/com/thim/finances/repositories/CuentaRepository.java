package com.thim.finances.repositories;

import com.thim.finances.dtos.TransaccionDTO;
import com.thim.finances.model.entities.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, String> {
    @Query("SELECT new com.thim.finances.dtos.TransaccionDTO(CASE WHEN TYPE(t) = Ingreso THEN 'INGRESO' ELSE 'GASTO' END, t.monto, t.descripcion, t.categoria.nombre, t.fecha, t.id) " +
            "FROM Cuenta c JOIN c.transacciones t WHERE c.id = :cuentaId")
    List<TransaccionDTO> findTransaccionesByCuentaId(@Param("cuentaId") String cuentaId);
    List<Cuenta> findByUsuarioId(String usuarioId);
}