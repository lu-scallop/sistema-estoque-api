package com.personal.crudapi.controller;

import com.personal.crudapi.dto.OrdemProducaoRequestDTO;
import com.personal.crudapi.entity.OrdemProducao;
import com.personal.crudapi.service.OrdemProducaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordens-producao")
public class OrdemProducaoController {

    @Autowired
    private OrdemProducaoService service;

    @PostMapping
    public ResponseEntity<OrdemProducao> criar(@RequestBody OrdemProducaoRequestDTO dto){
        return ResponseEntity.ok(service.criaOrderDeProducao(dto));
    }

    @PostMapping("/{id}/apontar")
    public ResponseEntity<Void> apontar(@PathVariable Long id, @RequestParam Long quantidade){
        service.apontaProducao(id, quantidade);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<OrdemProducao> listar(){ return service.listaTodasAsOrdens(); }
}
