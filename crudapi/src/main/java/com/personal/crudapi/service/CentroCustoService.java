package com.personal.crudapi.service;

import com.personal.crudapi.dto.CentroCustoRequestDTO;
import com.personal.crudapi.entity.CentroCusto;
import com.personal.crudapi.repository.CentroCustoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CentroCustoService {

    @Autowired
    private CentroCustoRepository repository;

    @Transactional
    public CentroCusto adicionaCentroDeCusto(CentroCustoRequestDTO dto){
        if(repository.existsByCodigoCentroCusto(dto.getCodigoCentroCusto())){
            throw new IllegalArgumentException("Código já cadastrado: " + dto.getCodigoCentroCusto());
        }
        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto(dto.getCodigoCentroCusto());
        centroCusto.setNome(dto.getNome());

        return repository.save(centroCusto);
    }
    @Transactional
    public CentroCusto atualizaCentroDeCusto(Long id, CentroCustoRequestDTO dto){
        CentroCusto cc = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Centro de Custo não encontrado: " + id));

        if(!cc.getCodigoCentroCusto().equals(dto.getCodigoCentroCusto()) &&
                repository.existsByCodigoCentroCusto(dto.getCodigoCentroCusto()))
        {
            throw new IllegalArgumentException("Código já cadastrado: " + dto.getCodigoCentroCusto());

        }
        cc.setCodigoCentroCusto(dto.getCodigoCentroCusto());
        cc.setNome(dto.getNome());

        return repository.save(cc);
    }

    @Transactional
    public void deletaCentroDeCusto(Long id){
        if(!repository.existsById(id)){
            throw new IllegalArgumentException("Centro de Custo não encontrado: " + id);
        }

        repository.deleteById(id);
    }

    public CentroCusto buscaPorCodigo(String codigo){
        return repository.findByCodigoCentroCusto(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Centro de Custo não encontrado: " + codigo));
    }

    @Transactional
    public List<CentroCusto> listaTodosOsCentrosDeCustos() { return repository.findAll(); }
}
