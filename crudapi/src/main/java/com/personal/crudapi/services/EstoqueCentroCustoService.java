package com.personal.crudapi.services;

import com.personal.crudapi.entities.CentroCusto;
import com.personal.crudapi.entities.EstoqueCentroCusto;
import com.personal.crudapi.entities.Material;
import com.personal.crudapi.repositories.EstoqueCentroCustoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstoqueCentroCustoService {

    @Autowired(required = true)
    private EstoqueCentroCustoRepository repository;

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
        if(quantidade <= 0)
            throw new IllegalArgumentException("Quantidade inválida");

        EstoqueCentroCusto estoque = adicionaOuObtem(material, centroCusto);
        estoque.setSaldo(estoque.getSaldo() + quantidade);
        repository.save(estoque);
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

    public List<EstoqueCentroCusto> listaTodosOsEstoques(){
        return repository.findAll();
    }

}
