package com.personal.crudapi.dto;

import com.personal.crudapi.entity.Material;
import com.personal.crudapi.entity.CentroCusto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstoqueCentroCustoDTO {
    private Material codigoMaterial;
    private CentroCusto codigoCentroCusto;
    private Integer quantidade;
}
