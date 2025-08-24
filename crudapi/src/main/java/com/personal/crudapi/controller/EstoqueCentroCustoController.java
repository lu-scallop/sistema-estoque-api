package com.personal.crudapi.controller;

import com.personal.crudapi.entity.EstoqueCentroCusto;
import com.personal.crudapi.service.EstoqueCentroCustoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/estoques")
public class EstoqueCentroCustoController {

    @Autowired
    private EstoqueCentroCustoService service;

    @GetMapping
    public List<EstoqueCentroCusto> buscar(
            @RequestParam(required = false) Long materialId,
            @RequestParam(required = false) Long centroId){
        return service.buscar(materialId, centroId);
    }
}
