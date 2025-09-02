package com.personal.crudapi.service;

import com.personal.crudapi.dto.OrdemProducaoDTO;
import com.personal.crudapi.dto.OrdemProducaoRequestDTO;
import com.personal.crudapi.entity.Material;
import com.personal.crudapi.entity.OrdemProducao;
import com.personal.crudapi.enums.StatusOrdem;
import com.personal.crudapi.repository.MaterialRepository;
import com.personal.crudapi.repository.OrdemProducaoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdemProducaoService {

    @Autowired
    private OrdemProducaoRepository repository;
    @Autowired
    private MaterialRepository materialRepository;

    @Transactional
    public OrdemProducao criaOrderDeProducao(OrdemProducaoRequestDTO dto){
        OrdemProducao op = new OrdemProducao();

        Material material = materialRepository.findByCodigoMaterial(dto.getCodigoMaterial())
                        .orElseThrow(() -> new IllegalArgumentException("Código do material não encontrado: " + dto.getCodigoMaterial()));


        op.setCodigoProducao(dto.getCodigoProducao());
        op.setMaterial(material);
        op.setQuantidadePlanejada(dto.getQuantidadePlanejada());
        op.setQuantidadeConcluida(0L);
        op.setStatus(StatusOrdem.LIBERADA);

        return repository.save(op);

    }

    @Transactional
    public void apontaProducao(Long id, Long quantidade){
        if (quantidade == null || quantidade <= 0)
            throw new IllegalArgumentException("Quantidade inválida");

        var ordemProducao = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("O.P não encontrada"));

        long novaQuantidade = ordemProducao.getQuantidadeConcluida() + quantidade;

        if (novaQuantidade > ordemProducao.getQuantidadePlanejada())
            throw new IllegalArgumentException("Não pode exceder a quantidade planejada");

        ordemProducao.setQuantidadeConcluida(novaQuantidade);

        if (novaQuantidade == ordemProducao.getQuantidadePlanejada())
            ordemProducao.setStatus(StatusOrdem.CONCLUIDA);

        repository.save(ordemProducao);
    }

    public List<OrdemProducao> listaTodasAsOrdens() { return repository.findAll(); }


}
