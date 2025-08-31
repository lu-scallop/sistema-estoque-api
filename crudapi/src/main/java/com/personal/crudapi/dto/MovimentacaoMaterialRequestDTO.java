package com.personal.crudapi.dto;

import com.personal.crudapi.enums.TipoMovimentacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MovimentacaoMaterialRequestDTO {
    @NotNull
    private String codigoMaterial;
    @NotNull
    private String codigoCentroOrigem;
    @NotNull
    private String codigoCentroDestino;
    @NotNull @Positive
    private Long quantidadeMovimentada;
    @NotNull
    private TipoMovimentacao tipo;
    private String observacao;
}
