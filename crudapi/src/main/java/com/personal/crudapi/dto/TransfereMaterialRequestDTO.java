package com.personal.crudapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransfereMaterialRequestDTO {
    @NotNull
    private String codigoMaterial;
    @NotNull
    private String codigoCentroOrigem;
    @NotNull
    private String codigoCentroDestino;
    @NotNull @Positive
    private Long quantidadeMovimentada;
    private String observacao;
}
