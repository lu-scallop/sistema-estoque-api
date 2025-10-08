package com.personal.crudapi.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MaterialRequestDTO {
    @NotBlank
    private String codigoMaterial;
    @NotBlank
    private String nome;
    @NotBlank
    private String tipo;
}
