package com.personal.crudapi.controllers;

import com.personal.crudapi.entities.EstoqueCentroCusto;
import com.personal.crudapi.services.EstoqueCentroCustoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/estoques")
public class EstoqueCentroCustoController {

    @Autowired
    private EstoqueCentroCustoService service;

    @GetMapping
    public List<EstoqueCentroCusto> buscar(){
        return service.listaTodosOsEstoques();
    }
}
