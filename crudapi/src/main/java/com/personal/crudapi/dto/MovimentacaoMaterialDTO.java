package com.personal.crudapi.dto;

import com.personal.crudapi.entity.CentroCusto;
import com.personal.crudapi.entity.Material;
import com.personal.crudapi.enums.TipoMovimentacao;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
