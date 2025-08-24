package com.personal.crudapi.service;

import com.personal.crudapi.dto.EstoqueCentroCustoDTO;
import com.personal.crudapi.entity.CentroCusto;
import com.personal.crudapi.entity.EstoqueCentroCusto;
import com.personal.crudapi.entity.Material;
import com.personal.crudapi.repository.EstoqueCentroCustoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstoqueCentroCustoService {

    @Autowired(required = true)
    private final EstoqueCentroCustoRepository repository;

    public EstoqueCentroCustoService(EstoqueCentroCustoRepository repository){
        this.repository = repository;

    }

    @Transactional
    public EstoqueCentroCusto adicionaOuObtem(Material material, CentroCusto centroCusto){
        return repository.findByMaterialAndCentroCusto(material, centroCusto)
                .orElseGet(() -> {
                    EstoqueCentroCusto estoqueCentroCusto = new EstoqueCentroCusto();
                    estoqueCentroCusto.setMaterial(material);
                    estoqueCentroCusto.setCentroCusto(centroCusto);
                    estoqueCentroCusto.setSaldo(0L);
                    return repository.save(estoqueCentroCusto);
                });
    }

    @Transactional
    public void creditaSaldo(Material material, CentroCusto centroCusto, long quantidade){
        if(quantidade <= 0) throw new IllegalArgumentException("Quantidade inválida");
        var e = adicionaOuObtem(material, centroCusto);
        e.setSaldo(e.getSaldo() + quantidade);
        repository.save(e);
    }

    @Transactional
    public void debitaSaldo(Material material, CentroCusto centroCusto, long quantidade){
        if(quantidade <= 0) throw new IllegalArgumentException("Quantidade inválida");
        var e = adicionaOuObtem(material, centroCusto);
        if (e.getSaldo() < quantidade) throw new IllegalArgumentException("Saldo insuficiente");
        e.setSaldo(e.getSaldo() - quantidade);
        repository.save(e);
    }

    @Transactional
    public void transfereSaldo(Material material, CentroCusto origem, CentroCusto destino, long quantidade){
        if (origem.equals(destino)) throw new IllegalArgumentException("Origem = Destino");
        debitaSaldo(material, origem, quantidade);
        creditaSaldo(material, destino, quantidade);

    }

    public List<EstoqueCentroCusto> buscar(Long materialId, Long centroId){
        return repository.findAll();
    }

}
