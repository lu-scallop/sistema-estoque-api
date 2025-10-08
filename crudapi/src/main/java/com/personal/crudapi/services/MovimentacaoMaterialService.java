package com.personal.crudapi.services;

import com.personal.crudapi.dtos.requests.EntradaMaterialRequestDTO;
import com.personal.crudapi.dtos.requests.SaidaMaterialRequestDTO;
import com.personal.crudapi.dtos.requests.TransfereMaterialRequestDTO;
import com.personal.crudapi.entities.CentroCusto;
import com.personal.crudapi.entities.Material;
import com.personal.crudapi.entities.MovimentacaoMaterial;
import com.personal.crudapi.enums.TipoMovimentacao;
import com.personal.crudapi.repositories.CentroCustoRepository;
import com.personal.crudapi.repositories.MaterialRepository;
import com.personal.crudapi.repositories.MovimentacaoMaterialRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public MovimentacaoMaterial transfereMaterial(TransfereMaterialRequestDTO dto)
    {
        if (dto.getQuantidadeMovimentada() <= 0)
            throw new IllegalArgumentException("Quantidade inválida.");

        Material material = verificaMaterial(dto.getCodigoMaterial());
        CentroCusto centroOrigem = verificaCentro(dto.getCodigoCentroOrigem());
        CentroCusto centroDestino = verificaCentro(dto.getCodigoCentroDestino());

        if (dto.getCodigoCentroOrigem().equals(dto.getCodigoCentroDestino()))
            throw new IllegalArgumentException("O centro de origem e destino NÃO podem ser iguais.");

        estoqueService.transfereSaldo(material, centroOrigem, centroDestino, dto.getQuantidadeMovimentada());

        MovimentacaoMaterial movimentacaoMaterial = new MovimentacaoMaterial();
        movimentacaoMaterial.setMaterial(material);
        movimentacaoMaterial.setCentroOrigem(centroOrigem);
        movimentacaoMaterial.setCentroDestino(centroDestino);
        movimentacaoMaterial.setQuantidadeMovimentada(dto.getQuantidadeMovimentada());
        movimentacaoMaterial.setTipo(TipoMovimentacao.TRANSFERENCIA);
        movimentacaoMaterial.setObservacao(dto.getObservacao());
        movimentacaoMaterial.setData(new Date());
        return repository.save(movimentacaoMaterial);

    }

    @Transactional
    public MovimentacaoMaterial entraMaterial(EntradaMaterialRequestDTO dto)
    {
        if (dto.getQuantidadeMovimentada() <= 0)
            throw new IllegalArgumentException("Quantidade inválida.");

        Material material = verificaMaterial(dto.getCodigoMaterial());
        CentroCusto centroDestino = verificaCentro(dto.getCodigoCentroDestino());

        estoqueService.creditaSaldo(material, centroDestino, dto.getQuantidadeMovimentada());

        MovimentacaoMaterial movimentacaoMaterial = new MovimentacaoMaterial();
        movimentacaoMaterial.setMaterial(material);
        movimentacaoMaterial.setCentroDestino(centroDestino);
        movimentacaoMaterial.setQuantidadeMovimentada(dto.getQuantidadeMovimentada());
        movimentacaoMaterial.setTipo(TipoMovimentacao.ENTRADA);
        movimentacaoMaterial.setObservacao(dto.getObservacao());
        movimentacaoMaterial.setData(new Date());
        return repository.save(movimentacaoMaterial);

    }

    @Transactional
    public MovimentacaoMaterial saiMaterial(SaidaMaterialRequestDTO dto)
    {
        if (dto.getQuantidadeMovimentada() <= 0)
            throw new IllegalArgumentException("Quantidade inválida.");

        Material material = verificaMaterial(dto.getCodigoMaterial());
        CentroCusto centroOrigem = verificaCentro(dto.getCodigoCentroOrigem());

        estoqueService.debitaSaldo(material, centroOrigem, dto.getQuantidadeMovimentada());

        MovimentacaoMaterial movimentacaoMaterial = new MovimentacaoMaterial();
        movimentacaoMaterial.setMaterial(material);
        movimentacaoMaterial.setCentroOrigem(centroOrigem);
        movimentacaoMaterial.setQuantidadeMovimentada(dto.getQuantidadeMovimentada());
        movimentacaoMaterial.setTipo(TipoMovimentacao.SAIDA);
        movimentacaoMaterial.setObservacao(dto.getObservacao());
        movimentacaoMaterial.setData(new Date());
        return repository.save(movimentacaoMaterial);


    }


    private Material verificaMaterial (String codigoMaterial){
        return materialRepository.findByCodigoMaterial(codigoMaterial)
                .orElseThrow(() -> new IllegalArgumentException("Material não encontrado: " + codigoMaterial));


    }
    private CentroCusto verificaCentro (String codigoCentroCusto){
        return centroCustoRepository.findByCodigoCentroCusto(codigoCentroCusto)
                .orElseThrow(() -> new IllegalArgumentException("Centro de Custo não encontrado: " + codigoCentroCusto));
    }


    public List<MovimentacaoMaterial> listaTodasAsMovimentacoes() { return repository.findAll();}
}
