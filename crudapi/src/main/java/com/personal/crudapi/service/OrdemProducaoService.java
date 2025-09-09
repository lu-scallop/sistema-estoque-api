package com.personal.crudapi.service;

import com.personal.crudapi.dto.requests.OrdemProducaoRequestDTO;
import com.personal.crudapi.entity.*;
import com.personal.crudapi.enums.StatusOrdem;
import com.personal.crudapi.repository.CentroCustoRepository;
import com.personal.crudapi.repository.MaterialRepository;
import com.personal.crudapi.repository.MovimentacaoMaterialRepository;
import com.personal.crudapi.repository.OrdemProducaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class OrdemProducaoService {

    @Autowired
    private OrdemProducaoRepository repository;
    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private CentroCustoRepository centroCustoRepository;
    @Autowired
    private MovimentacaoMaterialRepository movRepository;
    @Autowired
    private EstoqueCentroCustoService estoqueCentroCustoService;

    @Transactional
    public OrdemProducao criaOrderDeProducao(OrdemProducaoRequestDTO dto){
        OrdemProducao op = new OrdemProducao();
        Material material = materialRepository.findByCodigoMaterial(dto.getCodigoMaterial())
                        .orElseThrow(() -> new IllegalArgumentException("Código do material não encontrado: " + dto.getCodigoMaterial()));
        CentroCusto cc = centroCustoRepository.findByCodigoCentroCusto(dto.getCodigoCentroCusto())
                        .orElseThrow(() -> new IllegalArgumentException("Códido do Centro de Custo não encontrado: " + dto.getCodigoCentroCusto()));
        if (dto.getQuantidadePlanejada() < 1) { throw new IllegalArgumentException("Quantidade não deve ser menor que 1: " + dto.getQuantidadePlanejada()); }

        op.setCodigoProducao(dto.getCodigoProducao());
        op.setMaterial(material);
        op.setCentroCusto(cc);
        op.setQuantidadePlanejada(dto.getQuantidadePlanejada());
        op.setQuantidadeConcluida(0L);
        op.setStatus(StatusOrdem.LIBERADA);
        op.setDataAbertura(Instant.now());

        return repository.save(op);

    }

    @Transactional
    public void apontaProducao(Long id, Long quantidadeParaApontar){
        if (quantidadeParaApontar == null || quantidadeParaApontar < 1)
            throw new IllegalArgumentException("Quantidade inválida");

        OrdemProducao op = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("O.P não encontrada"));

        if (op.getStatus() == StatusOrdem.CONCLUIDA) {
            throw new IllegalArgumentException("Não é possível realizar apontamentos numa O.P já concluída: "
                    + op.getId());
        }

        EstoqueCentroCusto saldoDisponivelNoCentroDeCusto = estoqueCentroCustoService.adicionaOuObtem(op.getMaterial(), op.getCentroCusto());
        Long saldoDisponivel = saldoDisponivelNoCentroDeCusto.getSaldo();

        if (quantidadeParaApontar > saldoDisponivel)
            throw new IllegalArgumentException("Não há saldo suficiente para essa operação.");

        long novaQuantidade = op.getQuantidadeConcluida() + quantidadeParaApontar;

        if (novaQuantidade > op.getQuantidadePlanejada())
            throw new IllegalArgumentException("Não pode exceder a quantidade planejada.");

        op.setQuantidadeConcluida(novaQuantidade);
        estoqueCentroCustoService.debitaSaldo(op.getMaterial(), op.getCentroCusto(), quantidadeParaApontar);

        if (novaQuantidade == op.getQuantidadePlanejada()){
            op.setStatus(StatusOrdem.CONCLUIDA);
            op.setDataFechamento(Instant.now());
        } else {
            op.setStatus(StatusOrdem.EM_ANDAMENTO);
        }
        repository.save(op);
    }

    @Transactional
    public void deletarOrdemProducao(Long id){
        OrdemProducao op = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("O.P não encontrada."));
        if (op.getQuantidadeConcluida() > 0)
            throw new IllegalArgumentException("O.P não pode ser deletada pois já foi iniciada.");
        repository.deleteById(id);

    }

    public List<OrdemProducao> listaTodasAsOrdens() { return repository.findAll(); }


}
