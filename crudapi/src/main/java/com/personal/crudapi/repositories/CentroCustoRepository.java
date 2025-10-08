package com.personal.crudapi.repositories;

import com.personal.crudapi.entities.CentroCusto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CentroCustoRepository extends JpaRepository<CentroCusto, Long> {
    Optional<CentroCusto> findByCodigoCentroCusto(String codigo);
    boolean existsByCodigoCentroCusto(String codigo);
}
