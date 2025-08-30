package com.personal.crudapi.controller;

import com.personal.crudapi.dto.ReservaEstoqueDTO;
import com.personal.crudapi.entity.ReservaEstoque;
import com.personal.crudapi.service.ReservaEstoqueService;
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
    public ResponseEntity<ReservaEstoque> criar(@RequestBody ReservaEstoqueDTO dto){
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
