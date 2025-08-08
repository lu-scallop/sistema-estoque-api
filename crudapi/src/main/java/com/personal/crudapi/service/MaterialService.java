package com.personal.crudapi.service;

import com.personal.crudapi.dto.MaterialDTO;
import com.personal.crudapi.entity.Material;
import com.personal.crudapi.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MaterialService {

    @Autowired
    private MaterialRepository repository;


    public Material adicionaNovoMaterial(MaterialDTO materialDTO){
        Material material = new Material();
        material.setCodigoMaterial(materialDTO.getCodigoMaterial());
        material.setNome(materialDTO.getNome());
        material.setTipo(materialDTO.getTipo());

        return repository.save(material);
    }

    public void deletaMaterial(MaterialDTO materialDTO){
        Material material = new Material();
        material.setCodigoMaterial(materialDTO.getCodigoMaterial());
        repository.delete(material);
    }

    public Material atualizaMaterial(MaterialDTO materialDTO){
        Material material = new Material();
        material.setCodigoMaterial(materialDTO.getCodigoMaterial());

        return repository.save(material);
    }
    public List<Material> listaTodosOsMateriais(){
        return repository.findAll();
    }





}


