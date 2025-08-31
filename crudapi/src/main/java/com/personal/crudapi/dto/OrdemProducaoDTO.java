package com.personal.crudapi.dto;

import com.personal.crudapi.enums.StatusOrdem;
import lombok.Data;

@Data
public class OrdemProducaoDTO {
    private Long id;
    private String codigoProducao;
    private String codigoMaterial;
    private Long quantidadeConcluida;
    private Long quantidadePlanejada;
    private StatusOrdem status;
}
