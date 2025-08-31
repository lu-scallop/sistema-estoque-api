package com.personal.crudapi.dto;

import lombok.Data;

@Data
public class EstoqueCentroCustoDTO {
    private Long id;
    private String codigoMaterial;
    private String codigoCentroCusto;
    private Long saldo;
}
