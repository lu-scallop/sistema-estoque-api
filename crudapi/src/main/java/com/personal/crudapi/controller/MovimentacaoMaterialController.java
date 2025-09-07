package com.personal.crudapi.controller;

import com.personal.crudapi.dto.EntradaMaterialRequestDTO;
import com.personal.crudapi.dto.SaidaMaterialRequestDTO;
import com.personal.crudapi.dto.TransfereMaterialRequestDTO;
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

    @PostMapping("/transferir")
    public ResponseEntity<MovimentacaoMaterial> transferir(@RequestBody TransfereMaterialRequestDTO dto){
        return ResponseEntity.ok(service.transfereMaterial(dto));
    }

    @PostMapping("/entrada")
    public ResponseEntity<MovimentacaoMaterial> entrada(@RequestBody EntradaMaterialRequestDTO dto){
        return ResponseEntity.ok(service.entraMaterial(dto));
    }

    @PostMapping("/saida")
    public ResponseEntity<MovimentacaoMaterial> saida(@RequestBody SaidaMaterialRequestDTO dto){
        return ResponseEntity.ok(service.saiMaterial(dto));
    }

    @GetMapping
    public List<MovimentacaoMaterial> listar(){
        return service.listaTodasAsMovimentacoes();
    }
}
