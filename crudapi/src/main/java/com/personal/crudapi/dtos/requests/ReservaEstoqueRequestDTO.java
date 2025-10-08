package com.personal.crudapi.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ReservaEstoqueRequestDTO {
    @NotNull
    private String codigoMaterial;
    @NotNull
    private String centroCustoOrigem;
    @NotNull
    private String centroCustoDestino;
    @Positive
    private Long quantidade;
    private String observacao;
}
