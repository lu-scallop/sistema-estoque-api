package com.personal.crudapi.repository;

import com.personal.crudapi.entity.CentroCusto;
import com.personal.crudapi.entity.Material;
import com.personal.crudapi.entity.ReservaEstoque;
import com.personal.crudapi.enums.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReservaEstoqueRepository extends JpaRepository<ReservaEstoque, Long> {
    @Query("SELECT COALESCE(SUM(r.quantidade), 0 " +
            "FROM ReservaEstoque r " +
            "WHERE r.material = :material " +
            "AND r.origem = :origem " +
            "AND r.status IN :status")
    Long reservadoEmAberto (@Param("material") Material material,
                            @Param("origem")CentroCusto origem,
                            @Param("statuses")Collection<StatusReserva> statuses);

    List<ReservaEstoque> findByStatus(StatusReserva status);
}
