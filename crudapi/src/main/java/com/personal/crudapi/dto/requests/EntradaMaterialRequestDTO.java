package com.personal.crudapi.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class EntradaMaterialRequestDTO {
    @NotNull
    private String codigoMaterial;
    @NotNull
    private String codigoCentroDestino;
    @NotNull @Positive
    private Long quantidadeMovimentada;
    private String observacao;
}
