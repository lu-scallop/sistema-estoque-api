package com.personal.crudapi.dto;

import com.personal.crudapi.entity.CentroCusto;
import com.personal.crudapi.entity.Material;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovimentacaoMaterialDTO {
    private Material codigoMaterial;
    private CentroCusto centroOrigem;
    private CentroCusto centroDestino;
    private Integer quantidadeMovimentada;
    private String observacao;
}
