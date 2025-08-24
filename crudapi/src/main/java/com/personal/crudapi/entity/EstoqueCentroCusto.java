package com.personal.crudapi.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class EstoqueCentroCusto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private CentroCusto centroCusto;

    @ManyToOne(optional = false)
    private Material material;

    private Long saldo = 0L;
}
