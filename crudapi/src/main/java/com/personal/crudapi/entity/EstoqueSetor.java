package com.personal.crudapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class EstoqueSetor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private DispositivoIoT codigoMaterial;
    private SetorFabrica codigoSetor;
    private Integer quantidade;
}
