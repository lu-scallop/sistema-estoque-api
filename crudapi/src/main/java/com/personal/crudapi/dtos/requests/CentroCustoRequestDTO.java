package com.personal.crudapi.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CentroCustoRequestDTO {
    @NotBlank
    private String codigoCentroCusto;
    @NotBlank
    private String nome;
}
