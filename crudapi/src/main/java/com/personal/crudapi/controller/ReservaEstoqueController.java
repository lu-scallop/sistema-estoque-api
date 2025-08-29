package com.personal.crudapi.controller;

import com.personal.crudapi.dto.ReservaEstoqueDTO;
import com.personal.crudapi.entity.ReservaEstoque;
import com.personal.crudapi.service.ReservaEstoqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservas")
public class ReservaEstoqueController {

    @Autowired
    private ReservaEstoqueService service;

    @PostMapping
    public ResponseEntity<ReservaEstoque> criar(@RequestBody ReservaEstoqueDTO dto){
        return ResponseEntity.ok(service.criaReserva(dto));
    }


}
