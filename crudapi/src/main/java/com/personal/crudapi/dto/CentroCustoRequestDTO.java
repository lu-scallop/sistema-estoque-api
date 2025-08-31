package com.personal.crudapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CentroCustoRequestDTO {
    @NotBlank
    private String codigoCentroCusto;
    @NotBlank
    private String nome;
}
