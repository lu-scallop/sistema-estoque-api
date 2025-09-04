package com.personal.crudapi.repository;

import com.personal.crudapi.entity.OrdemProducao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdemProducaoRepository extends JpaRepository<OrdemProducao, Long> {
}
