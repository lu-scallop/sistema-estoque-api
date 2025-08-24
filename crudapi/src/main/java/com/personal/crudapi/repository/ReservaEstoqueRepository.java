package com.personal.crudapi.repository;

import com.personal.crudapi.entity.ReservaEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservaEstoqueRepository extends JpaRepository<ReservaEstoque, Long> {
}
