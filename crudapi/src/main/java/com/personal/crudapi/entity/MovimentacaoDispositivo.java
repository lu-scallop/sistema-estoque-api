package com.personal.crudapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MovimentacaoDispositivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private DispositivoIoT codigoMaterial;
    private SetorFabrica setorOrigem;
    private SetorFabrica setorDestino;
    private Integer quantidadeMovimentada;
    private String observacao;
}
