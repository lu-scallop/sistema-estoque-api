package com.personal.crudapi.entity;

import com.personal.crudapi.enums.TipoMovimentacao;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class MovimentacaoMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Material material;

    @ManyToOne
    private CentroCusto centroOrigem;

    @ManyToOne
    private CentroCusto centroDestino;

    private Long quantidadeMovimentada;

    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipo;

    @Temporal(TemporalType.TIMESTAMP)
    private Date data;

    private String observacao;
}
