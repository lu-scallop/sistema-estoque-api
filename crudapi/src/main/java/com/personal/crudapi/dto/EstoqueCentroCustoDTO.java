package com.personal.crudapi.dto;

import com.personal.crudapi.entity.Material;
import com.personal.crudapi.entity.CentroCusto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class EstoqueCentroCustoDTO {
    private Long id;
    private Material material;
    private CentroCusto centroCusto;
    private Long saldo;
}
