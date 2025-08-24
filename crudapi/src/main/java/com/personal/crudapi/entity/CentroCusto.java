package com.personal.crudapi.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CentroCusto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigoCentroCusto;
    private String nome;
}
