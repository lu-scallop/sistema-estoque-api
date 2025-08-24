package com.personal.crudapi.service;

import com.personal.crudapi.dto.MovimentacaoMaterialDTO;
import com.personal.crudapi.entity.MovimentacaoMaterial;
import com.personal.crudapi.repository.MovimentacaoMaterialRepository;
import lombok.RequiredArgsConstructor;
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

    public MovimentacaoMaterial movimentaMaterial(MovimentacaoMaterialDTO movimentacaoMaterialDTO){
        MovimentacaoMaterial movimentacaoMaterial = new MovimentacaoMaterial();

        if (movimentacaoMaterialDTO.getQuantidadeMovimentada()<=0)
            throw new IllegalArgumentException("Não há nada para movimentar.");
        if (movimentacaoMaterialDTO.getTipo() == null)
            throw new IllegalArgumentException("Selecione o tipo de movimentação.");
        if (movimentacaoMaterialDTO.getMaterial() == null)
            throw new IllegalArgumentException("O código do material é obrigatório.");

        movimentacaoMaterial.setMaterial(movimentacaoMaterialDTO.getMaterial());
        movimentacaoMaterial.setCentroOrigem(movimentacaoMaterialDTO.getCentroOrigem());
        movimentacaoMaterial.setCentroDestino(movimentacaoMaterialDTO.getCentroDestino());
        movimentacaoMaterial.setQuantidadeMovimentada(movimentacaoMaterialDTO.getQuantidadeMovimentada());
        movimentacaoMaterial.setTipo(movimentacaoMaterialDTO.getTipo());
        movimentacaoMaterial.setData(new Date());
        movimentacaoMaterial.setObservacao(movimentacaoMaterialDTO.getObservacao());


        switch (movimentacaoMaterialDTO.getTipo()){
            case TRANSFERENCIA -> {
                if(movimentacaoMaterialDTO.getCentroOrigem() == null)
                    throw new IllegalArgumentException("Centro origem é obrigatório.");
                if(movimentacaoMaterialDTO.getCentroDestino() == null)
                    throw new IllegalArgumentException("Centro destino é obrigatório.");
                if (movimentacaoMaterialDTO.getCentroOrigem().equals(movimentacaoMaterialDTO.getCentroDestino()))
                    throw new IllegalArgumentException("O centro de origem e destino NÃO podem ser iguais!");
                estoqueService.transfereSaldo(
                        movimentacaoMaterialDTO.getMaterial(),
                        movimentacaoMaterialDTO.getCentroOrigem(),
                        movimentacaoMaterialDTO.getCentroDestino(),
                        movimentacaoMaterialDTO.getQuantidadeMovimentada()
                );
            }
            case ENTRADA -> {
                if (movimentacaoMaterialDTO.getCentroDestino() == null)
                    throw new IllegalArgumentException("Centro destino é obrigatório.");
                estoqueService.creditaSaldo(
                        movimentacaoMaterialDTO.getMaterial(),
                        movimentacaoMaterialDTO.getCentroDestino(),
                        movimentacaoMaterialDTO.getQuantidadeMovimentada()
                );

            }
            case SAIDA_CLIENTE, SAIDA -> {
                if (movimentacaoMaterialDTO.getCentroOrigem() == null){
                    throw new IllegalArgumentException("Centro origem é obrigatório.");
                }
                estoqueService.debitaSaldo(
                        movimentacaoMaterialDTO.getMaterial(),
                        movimentacaoMaterialDTO.getCentroOrigem(),
                        movimentacaoMaterialDTO.getQuantidadeMovimentada()
                );
            }

        }
        return repository.save(movimentacaoMaterial);
    }

    public List<MovimentacaoMaterial> listaTodasAsMovimentacoes() { return repository.findAll();}
}
