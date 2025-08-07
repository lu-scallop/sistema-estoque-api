package com.personal.crudapi.repository;

import com.personal.crudapi.entity.DispositivoIoT;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispositivoRepository extends JpaRepository<DispositivoIoT, Long> {

}
