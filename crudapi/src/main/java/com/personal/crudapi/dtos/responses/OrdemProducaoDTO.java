package com.personal.crudapi.dtos.responses;

import com.personal.crudapi.enums.StatusOrdem;
import lombok.Data;

import java.time.Instant;

@Data
public class OrdemProducaoDTO {
    private Long id;
    private String codigoProducao;
    private String codigoMaterial;
    private Long quantidadeConcluida;
    private Long quantidadePlanejada;
    private StatusOrdem status;
    private Instant dataAbertura;
    private Instant dataFechamento;
}
