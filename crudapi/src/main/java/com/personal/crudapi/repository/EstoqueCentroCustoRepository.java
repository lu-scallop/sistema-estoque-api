package com.personal.crudapi.repository;

import com.personal.crudapi.entity.CentroCusto;
import com.personal.crudapi.entity.EstoqueCentroCusto;
import com.personal.crudapi.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstoqueCentroCustoRepository extends JpaRepository<EstoqueCentroCusto, Long> {
    Optional<EstoqueCentroCusto> findByMaterialAndCentroCusto(Material material, CentroCusto centroCusto);
}
