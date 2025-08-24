package com.personal.crudapi.dto;

import lombok.Data;

@Data
public class MaterialDTO {
    private Long id;
    private String codigoMaterial;
    private String nome;
    private String tipo;
}
