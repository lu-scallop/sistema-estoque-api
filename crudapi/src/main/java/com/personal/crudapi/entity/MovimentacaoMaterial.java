package com.personal.crudapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MovimentacaoMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Material codigoMaterial;
    private CentroCusto centroOrigem;
    private CentroCusto centroDestino;
    private Integer quantidadeMovimentada;
    private String observacao;
}
