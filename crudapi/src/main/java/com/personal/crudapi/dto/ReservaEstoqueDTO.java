package com.personal.crudapi.dto;

import com.personal.crudapi.entity.CentroCusto;
import com.personal.crudapi.entity.Material;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

import java.util.Date;

@Data
public class ReservaEstoqueDTO {
    private Material material;
    private CentroCusto centroCustoOrigem;
    private CentroCusto centroCustoDestino;
    private Long quantidade;
    private Date criadaEm;
    private Date dataAprovacao;
    private Date dataAtendimento;
}
