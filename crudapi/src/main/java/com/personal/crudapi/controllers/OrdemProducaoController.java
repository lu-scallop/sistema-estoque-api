package com.personal.crudapi.controllers;

import com.personal.crudapi.dtos.requests.OrdemProducaoRequestDTO;
import com.personal.crudapi.entities.OrdemProducao;
import com.personal.crudapi.services.OrdemProducaoService;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        service.deletarOrdemProducao(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<OrdemProducao> listar(){ return service.listaTodasAsOrdens(); }
}
