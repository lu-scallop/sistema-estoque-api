package com.personal.crudapi.dto;

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
