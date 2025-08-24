package com.personal.crudapi.repository;

import com.personal.crudapi.entity.CentroCusto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CentroCustoRepository extends JpaRepository<CentroCusto, Long> {
    Optional<CentroCusto> findByCodigoCentroCusto(String codigo);
    boolean existsByCodigoCentroCusto(String codigo);
}
