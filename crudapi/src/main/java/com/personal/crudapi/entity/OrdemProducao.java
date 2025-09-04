package com.personal.crudapi.entity;

import com.personal.crudapi.enums.StatusOrdem;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Data
public class OrdemProducao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigoProducao;

    @ManyToOne
    private Material material;

    @ManyToOne
    private CentroCusto centroCusto;

    private Long quantidadeConcluida;

    private Long quantidadePlanejada;

    @Enumerated(EnumType.STRING)
    private StatusOrdem status;

    private Instant dataAbertura;
    private Instant dataFechamento;
}
