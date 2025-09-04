package com.personal.crudapi.service;

import com.personal.crudapi.dto.MovimentacaoMaterialRequestDTO;
import com.personal.crudapi.entity.CentroCusto;
import com.personal.crudapi.entity.Material;
import com.personal.crudapi.entity.MovimentacaoMaterial;
import com.personal.crudapi.repository.CentroCustoRepository;
import com.personal.crudapi.repository.MaterialRepository;
import com.personal.crudapi.repository.MovimentacaoMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MovimentacaoMaterialService {
    @Autowired
    private MovimentacaoMaterialRepository repository;
    @Autowired
    private EstoqueCentroCustoService estoqueService;
    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private CentroCustoRepository centroCustoRepository;

    public MovimentacaoMaterial movimentaMaterial(MovimentacaoMaterialRequestDTO dto){
        MovimentacaoMaterial movimentacaoMaterial = new MovimentacaoMaterial();

        if (dto.getQuantidadeMovimentada()<=0)
            throw new IllegalArgumentException("Não há nada para movimentar.");
        if (dto.getTipo() == null)
            throw new IllegalArgumentException("Selecione o tipo de movimentação.");
        if (dto.getCodigoMaterial() == null)
            throw new IllegalArgumentException("O código do material é obrigatório.");
        Material material = materialRepository.findByCodigoMaterial(dto.getCodigoMaterial())
                        .orElseThrow(() -> new IllegalArgumentException("Material não encontrado: " + dto.getCodigoMaterial()));
        CentroCusto centroCustoOrigem = centroCustoRepository.findByCodigoCentroCusto(dto.getCodigoCentroOrigem())
                        .orElseThrow(() -> new IllegalArgumentException("Centro de Origem não encontrado: " + dto.getCodigoCentroOrigem()));
        CentroCusto centroCustoDestino = centroCustoRepository.findByCodigoCentroCusto(dto.getCodigoCentroOrigem())
                .orElseThrow(() -> new IllegalArgumentException("Centro de Destino não encontrado: " + dto.getCodigoCentroDestino()));


        movimentacaoMaterial.setMaterial(material);
        movimentacaoMaterial.setCentroOrigem(centroCustoOrigem);
        movimentacaoMaterial.setCentroDestino(centroCustoDestino);
        movimentacaoMaterial.setQuantidadeMovimentada(dto.getQuantidadeMovimentada());
        movimentacaoMaterial.setTipo(dto.getTipo());
        movimentacaoMaterial.setData(new Date());
        movimentacaoMaterial.setObservacao(dto.getObservacao());


        switch (dto.getTipo()){
            case TRANSFERENCIA -> {
                if(centroCustoOrigem == null)
                    throw new IllegalArgumentException("Centro origem é obrigatório.");
                if(centroCustoDestino == null)
                    throw new IllegalArgumentException("Centro destino é obrigatório.");
                if (centroCustoOrigem.equals(centroCustoDestino))
                    throw new IllegalArgumentException("O centro de origem e destino NÃO podem ser iguais!");
                estoqueService.transfereSaldo(
                        material,
                        centroCustoOrigem,
                        centroCustoDestino,
                        dto.getQuantidadeMovimentada()
                );
            }
            case ENTRADA -> {
                if (centroCustoDestino == null)
                    throw new IllegalArgumentException("Centro destino é obrigatório.");
                estoqueService.creditaSaldo(
                        material,
                        centroCustoDestino,
                        dto.getQuantidadeMovimentada()
                );

            }
            case SAIDA_CLIENTE, SAIDA -> {
                if (centroCustoOrigem== null){
                    throw new IllegalArgumentException("Centro origem é obrigatório.");
                }
                estoqueService.debitaSaldo(
                        material,
                        centroCustoOrigem,
                        dto.getQuantidadeMovimentada()
                );
            }

        }
        return repository.save(movimentacaoMaterial);
    }

    public List<MovimentacaoMaterial> listaTodasAsMovimentacoes() { return repository.findAll();}
}
