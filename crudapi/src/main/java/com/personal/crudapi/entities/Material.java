package com.personal.crudapi.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigoMaterial;
    private String nome;
    private String tipo;
}
