package com.personal.crudapi.dtos.responses;

import lombok.Data;

@Data
public class MaterialDTO {
    private Long id;
    private String codigoMaterial;
    private String nome;
    private String tipo;
}
