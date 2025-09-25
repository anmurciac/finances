package com.thim.finances.repositories;

import com.thim.finances.model.entities.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, String> {
    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Transaccion t " +
            "WHERE t.cuenta.usuario.id = :usuarioId AND TYPE(t) = Ingreso")
    BigDecimal sumIngresosByUsuario(String usuarioId);

    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Transaccion t " +
            "WHERE t.cuenta.usuario.id = :usuarioId AND TYPE(t) = Gasto")
    BigDecimal sumGastosByUsuario(String usuarioId);

    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Transaccion t " +
            "WHERE t.cuenta.usuario.id = :usuarioId AND TYPE(t) = Ingreso " +
            "AND YEAR(t.fecha) = :año AND MONTH(t.fecha) = :mes")
    BigDecimal sumIngresosByUsuarioAndPeriodo(String usuarioId, int año, int mes);

    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Transaccion t " +
            "WHERE t.cuenta.usuario.id = :usuarioId AND TYPE(t) = Gasto " +
            "AND YEAR(t.fecha) = :año AND MONTH(t.fecha) = :mes")
    BigDecimal sumGastosByUsuarioAndPeriodo(String usuarioId, int año, int mes);

    @Query("SELECT t FROM Transaccion t WHERE t.cuenta.usuario.id = :usuarioId AND t.categoria.id = :categoriaId")
    List<Transaccion> findByUsuarioIdAndCategoriaId(@Param("usuarioId") String usuarioId,
                                                    @Param("categoriaId") String categoriaId);
    List<Transaccion> findByCuentaId(String cuentaId);
    List<Transaccion> findByCuentaIdAndFechaBetween(String cuentaId, LocalDateTime inicio, LocalDateTime fin);
    List<Transaccion> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}