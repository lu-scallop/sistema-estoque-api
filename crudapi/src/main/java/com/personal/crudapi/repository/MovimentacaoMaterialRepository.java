package com.personal.crudapi.repository;

import com.personal.crudapi.entity.MovimentacaoMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoMaterialRepository extends JpaRepository<MovimentacaoMaterial, Long> {
}
