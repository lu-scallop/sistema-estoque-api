package com.personal.crudapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class EstoqueCentroCusto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Material codigoMaterial;
    private CentroCusto codigoCentroCusto;
    private Integer quantidade;
}
