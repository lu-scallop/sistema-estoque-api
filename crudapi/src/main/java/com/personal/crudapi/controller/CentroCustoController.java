package com.personal.crudapi.controller;

import com.personal.crudapi.dto.CentroCustoDTO;
import com.personal.crudapi.entity.CentroCusto;
import com.personal.crudapi.service.CentroCustoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/centros-de-custo")
public class CentroCustoController {
    @Autowired
    private CentroCustoService service;

    @PostMapping
    public ResponseEntity<CentroCusto> criar(@Valid @RequestBody CentroCustoDTO dto){
        CentroCusto salvo = service.adicionaCentroDeCusto(dto);
        URI location = URI.create("/centros-de-custo/"+salvo.getId());
        return ResponseEntity.created(location).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CentroCusto> atualizar(@PathVariable Long id,
                                                 @RequestBody CentroCustoDTO dto){
        return ResponseEntity.ok(service.atualizaCentroDeCusto(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        service.deletaCentroDeCusto(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{codigo}")
    public CentroCusto buscar(String codigo){
        return service.buscaPorCodigo(codigo);
    }

    @GetMapping
    public List<CentroCusto> listar() { return service.listaTodosOsCentrosDeCustos(); }

}
