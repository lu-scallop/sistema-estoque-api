package com.personal.crudapi.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrdemProducaoRequestDTO {
    @NotBlank
    private String codigoProducao;
    @NotNull
    private String codigoMaterial;
    @NotNull
    private String codigoCentroCusto;
    @NotNull @Positive
    private Long quantidadePlanejada;
}
