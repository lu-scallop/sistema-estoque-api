package com.personal.crudapi.services;

import com.personal.crudapi.dtos.requests.MaterialRequestDTO;
import com.personal.crudapi.entities.Material;
import com.personal.crudapi.repositories.MaterialRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MaterialService {

    @Autowired
    private MaterialRepository repository;

    @Transactional
    public Material adicionaOuAtualizaMaterial(MaterialRequestDTO dto){
        Material material = repository.findByCodigoMaterial(dto.getCodigoMaterial())
                        .orElseGet(Material::new);
        material.setCodigoMaterial(dto.getCodigoMaterial());
        material.setNome(dto.getNome());
        material.setTipo(dto.getTipo());

        return repository.save(material);
    }

    @Transactional
    public void deletaMaterial(Long id){
        if(!repository.existsById(id)){
            throw new IllegalArgumentException("Material não encontrado: " + id);
        }

        repository.deleteById(id);
    }

    public List<Material> listaTodosOsMateriais(){
        return repository.findAll();
    }

    public Material buscaPorCodigo(String codigo){
        return repository.findByCodigoMaterial(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Material não encontrado: "+codigo));

    }

}


