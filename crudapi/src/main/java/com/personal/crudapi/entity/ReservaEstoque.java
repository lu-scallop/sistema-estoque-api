package com.personal.crudapi.entity;

import com.personal.crudapi.enums.StatusReserva;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class ReservaEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Material material;
    @ManyToOne(optional = false)
    private CentroCusto centroCustoOrigem;
    @ManyToOne(optional = false)
    private CentroCusto centroCustoDestino;

    private Long quantidadeSolicitada;
    private Long quantidadeAtendida = 0L;

    @Enumerated(EnumType.STRING)
    private StatusReserva status = StatusReserva.ABERTA;

    /*
    @Temporal(TemporalType.TIMESTAMP)
    private Date criadaEm = new Date();

     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAprovacao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAtendimento;
}
