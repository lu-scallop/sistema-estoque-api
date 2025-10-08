package com.personal.crudapi.repositories;

import com.personal.crudapi.entities.ReservaEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaEstoqueRepository extends JpaRepository<ReservaEstoque, Long> {
}
