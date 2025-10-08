package com.personal.crudapi.repositories;

import com.personal.crudapi.entities.MovimentacaoMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoMaterialRepository extends JpaRepository<MovimentacaoMaterial, Long> {
}
