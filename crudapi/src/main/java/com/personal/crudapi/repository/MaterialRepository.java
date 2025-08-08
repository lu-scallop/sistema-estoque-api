package com.personal.crudapi.repository;

import com.personal.crudapi.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Long> {

}
