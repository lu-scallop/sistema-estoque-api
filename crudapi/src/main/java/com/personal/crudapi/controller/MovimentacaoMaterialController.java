package com.personal.crudapi.controller;

import com.personal.crudapi.dto.MovimentacaoMaterialDTO;
import com.personal.crudapi.entity.MovimentacaoMaterial;
import com.personal.crudapi.service.MovimentacaoMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimentacoes")
public class MovimentacaoMaterialController {

    @Autowired
    private MovimentacaoMaterialService service;

    @PostMapping
    public ResponseEntity<MovimentacaoMaterial> criar(@RequestBody MovimentacaoMaterialDTO dto){
        return ResponseEntity.ok(service.movimentaMaterial(dto));
    }
    @GetMapping
    public List<MovimentacaoMaterial> listar(){
        return service.listaTodasAsMovimentacoes();
    }
}
