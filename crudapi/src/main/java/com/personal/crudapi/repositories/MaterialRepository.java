package com.personal.crudapi.repositories;

import com.personal.crudapi.entities.Material;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    Optional<Material> findByCodigoMaterial(String codigo);

}
