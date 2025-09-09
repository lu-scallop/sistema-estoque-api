package com.personal.crudapi.dto.responses;

import lombok.Data;

@Data
public class EstoqueCentroCustoDTO {
    private Long id;
    private String codigoMaterial;
    private String codigoCentroCusto;
    private Long saldo;
}
