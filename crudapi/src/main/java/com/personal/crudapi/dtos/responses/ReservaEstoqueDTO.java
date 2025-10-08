package com.personal.crudapi.dtos.responses;

import com.personal.crudapi.enums.StatusReserva;
import lombok.Data;

import java.time.Instant;

@Data
public class ReservaEstoqueDTO {
    private Long id;
    private Long materialId;
    private String materialCodigo;
    private Long centroCustoOrigemId;
    private String centroOrigemCodigo;
    private Long centroCustoDestinoId;
    private String centroCustoDestinoCodigo;
    private Long quantidadeSolicitada;
    private Long quantidadeAtendida;
    private StatusReserva status;
    private Instant dataAprovacao;
    private Instant dataAtendimento;
}
