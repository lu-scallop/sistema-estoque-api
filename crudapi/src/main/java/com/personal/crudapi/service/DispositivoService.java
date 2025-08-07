package com.personal.crudapi.service;

import com.personal.crudapi.dto.DispositivoDTO;
import com.personal.crudapi.entity.DispositivoIoT;
import com.personal.crudapi.repository.DispositivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DispositivoService {

    @Autowired
    private DispositivoRepository repository;


    public DispositivoIoT adicionarNovoDispositivoIot(DispositivoDTO dispositivoDTO){
        DispositivoIoT dispositivoIoT = new DispositivoIoT();
        dispositivoIoT.setCodigoMaterial(dispositivoDTO.getCodigoMaterial());
        dispositivoIoT.setNome(dispositivoDTO.getNome());
        dispositivoIoT.setTipo(dispositivoDTO.getTipo());

        return repository.save(dispositivoIoT);
    }



    public List<DispositivoIoT> listarTodos(){
        return repository.findAll();
    }





}


