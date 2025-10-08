package com.personal.crudapi.controllers;

import com.personal.crudapi.dtos.requests.ReservaEstoqueRequestDTO;
import com.personal.crudapi.entities.ReservaEstoque;
import com.personal.crudapi.services.ReservaEstoqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaEstoqueController {

    @Autowired
    private ReservaEstoqueService service;

    @PostMapping
    public ResponseEntity<ReservaEstoque> criar(@RequestBody ReservaEstoqueRequestDTO dto){
        return ResponseEntity.ok(service.criaReserva(dto));
    }

    @PostMapping("/{id}/atender")
    public ResponseEntity<ReservaEstoque> atender(@PathVariable Long id, @RequestParam Long quantidadeParaAtender){
        service.atendePedidoDeReserva(id, quantidadeParaAtender);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<ReservaEstoque> listar(){ return service.listaTodasAsReservas(); }


}
