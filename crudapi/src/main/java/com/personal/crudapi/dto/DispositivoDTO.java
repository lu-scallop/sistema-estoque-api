package com.personal.crudapi.dto;

import com.personal.crudapi.entity.DispositivoIoT;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DispositivoDTO {
    private String codigoMaterial;
    private String nome;
    private String tipo;
}
