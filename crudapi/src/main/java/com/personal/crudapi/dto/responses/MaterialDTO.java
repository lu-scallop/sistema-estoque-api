package com.personal.crudapi.dto.responses;

import lombok.Data;

@Data
public class MaterialDTO {
    private Long id;
    private String codigoMaterial;
    private String nome;
    private String tipo;
}
