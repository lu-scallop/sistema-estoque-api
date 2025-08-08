package com.personal.crudapi.dto;

import com.personal.crudapi.entity.EstoqueCentroCusto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CentroCustoDTO {
    private String codigoCentroCusto;
    private String nome;
    private EstoqueCentroCusto deposito;

}
