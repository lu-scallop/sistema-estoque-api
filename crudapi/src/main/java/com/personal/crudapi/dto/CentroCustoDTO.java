package com.personal.crudapi.dto;

import com.personal.crudapi.entity.EstoqueCentroCusto;
import lombok.Data;

@Data
public class CentroCustoDTO {
    private Long id;
    private String codigoCentroCusto;
    private String nome;
    private EstoqueCentroCusto deposito;
}
