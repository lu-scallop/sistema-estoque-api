package com.personal.crudapi.repository;

import com.personal.crudapi.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    Optional<Material> findByCodigoMaterial(String codigo);

}
