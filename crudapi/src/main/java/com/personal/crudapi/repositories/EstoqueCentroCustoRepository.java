package com.personal.crudapi.repositories;

import com.personal.crudapi.entities.CentroCusto;
import com.personal.crudapi.entities.EstoqueCentroCusto;
import com.personal.crudapi.entities.Material;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstoqueCentroCustoRepository extends JpaRepository<EstoqueCentroCusto, Long> {
    Optional<EstoqueCentroCusto> findByMaterialAndCentroCusto(Material material, CentroCusto centroCusto);
}
