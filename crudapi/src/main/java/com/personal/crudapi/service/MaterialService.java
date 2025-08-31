package com.personal.crudapi.service;

import com.personal.crudapi.dto.MaterialDTO;
import com.personal.crudapi.entity.Material;
import com.personal.crudapi.repository.MaterialRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MaterialService {

    @Autowired
    private MaterialRepository repository;

    @Transactional
    public Material adicionaOuAtualizaMaterial(MaterialDTO materialDTO){
        Material material = repository.findByCodigoMaterial(materialDTO.getCodigoMaterial())
                        .orElseGet(Material::new);
        material.setCodigoMaterial(materialDTO.getCodigoMaterial());
        material.setNome(materialDTO.getNome());
        material.setTipo(materialDTO.getTipo());

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


