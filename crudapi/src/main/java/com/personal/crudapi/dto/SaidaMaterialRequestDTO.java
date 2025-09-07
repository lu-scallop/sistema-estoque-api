package com.personal.crudapi.dto;

import com.personal.crudapi.enums.TipoMovimentacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SaidaMaterialRequestDTO {
    @NotNull
    private String codigoMaterial;
    @NotNull
    private String codigoCentroOrigem;
    @NotNull @Positive
    private Long quantidadeMovimentada;
    private String observacao;
}
