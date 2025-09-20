package com.personal.crudapi.controller;

import com.personal.crudapi.dto.requests.MaterialRequestDTO;
import com.personal.crudapi.entity.Material;
import com.personal.crudapi.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materiais")
public class MaterialController {

    @Autowired
    private MaterialService service;

    @PostMapping
    public ResponseEntity<Material> criar(@RequestBody MaterialRequestDTO dto){
        return ResponseEntity.ok(service.adicionaOuAtualizaMaterial(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        service.deletaMaterial(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{codigo}")
    public Material buscar(String codigo){
        return service.buscaPorCodigo(codigo);
    }

    @GetMapping
    public List<Material> listar(){ return service.listaTodosOsMateriais(); }
}
