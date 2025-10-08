package com.personal.crudapi.dtos.responses;

import com.personal.crudapi.entities.CentroCusto;
import com.personal.crudapi.entities.Material;
import com.personal.crudapi.enums.TipoMovimentacao;
import lombok.Data;

import java.util.Date;

@Data
public class MovimentacaoMaterialDTO {
    private Long id;
    private Material material;
    private CentroCusto centroOrigem;
    private CentroCusto centroDestino;
    private Long quantidadeMovimentada;
    private TipoMovimentacao tipo;
    private Date data;
    private String observacao;
}
