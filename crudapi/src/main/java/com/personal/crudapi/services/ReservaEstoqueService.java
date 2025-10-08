package com.personal.crudapi.services;

import com.personal.crudapi.dtos.requests.ReservaEstoqueRequestDTO;
import com.personal.crudapi.entities.CentroCusto;
import com.personal.crudapi.entities.Material;
import com.personal.crudapi.entities.MovimentacaoMaterial;
import com.personal.crudapi.entities.ReservaEstoque;
import com.personal.crudapi.enums.StatusReserva;
import com.personal.crudapi.enums.TipoMovimentacao;
import com.personal.crudapi.repositories.CentroCustoRepository;
import com.personal.crudapi.repositories.MaterialRepository;
import com.personal.crudapi.repositories.MovimentacaoMaterialRepository;
import com.personal.crudapi.repositories.ReservaEstoqueRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReservaEstoqueService {

    @Autowired
    private ReservaEstoqueRepository repository;
    @Autowired
    private EstoqueCentroCustoService estoqueService;
    @Autowired
    private MovimentacaoMaterialRepository movimentacaoMaterialRepository;
    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private CentroCustoRepository centroCustoRepository;

    @Transactional
    public ReservaEstoque criaReserva(ReservaEstoqueRequestDTO dto){
        ReservaEstoque reserva = new ReservaEstoque();

        Material material = materialRepository.findByCodigoMaterial(dto.getCodigoMaterial())
                .orElseThrow(() -> new IllegalArgumentException("Código do material não encontrado: " + dto.getCodigoMaterial()));
        CentroCusto centroCustoOrigem = centroCustoRepository.findByCodigoCentroCusto(dto.getCentroCustoOrigem())
                .orElseThrow(() -> new IllegalArgumentException("Centro de Origem não encontrado: " + dto.getCentroCustoOrigem()));
        CentroCusto centroCustoDestino = centroCustoRepository.findByCodigoCentroCusto(dto.getCentroCustoDestino())
                .orElseThrow(() -> new IllegalArgumentException("Centro de Destino não encontrado: " + dto.getCentroCustoDestino()));


        if (material == null)
            throw new IllegalArgumentException("Material não encontrado.");
        if (centroCustoOrigem == null)
            throw new IllegalArgumentException("Centro de origem não encontrado.");
        if (dto.getCentroCustoDestino() == null)
            throw new IllegalArgumentException("Centro de destino não encontrado.");
        if (dto.getCentroCustoOrigem().equals(dto.getCentroCustoDestino()))
            throw new IllegalArgumentException("Origem e Destino NÃO devem ser iguais.");

        reserva.setMaterial(material);
        reserva.setCentroCustoOrigem(centroCustoOrigem);
        reserva.setCentroCustoDestino(centroCustoDestino);
        reserva.setQuantidadeSolicitada(dto.getQuantidade());
        reserva.setStatus(StatusReserva.ABERTA);
        return repository.save(reserva);

    }

    @Transactional
    public void atendePedidoDeReserva(Long id, Long quantidadeParaAtender){
        if (quantidadeParaAtender == null || quantidadeParaAtender <= 0){
            throw new IllegalArgumentException("Quantidade para atender solicitação é inválida");
        }
        ReservaEstoque reserva = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido de reserva não encontrado: id = " + id));
        if (reserva.getStatus() != StatusReserva.ABERTA){
            throw new IllegalArgumentException("A reserva já foi atendida totalmente");
        }

        long quantidadeRestante = reserva.getQuantidadeSolicitada() - reserva.getQuantidadeAtendida();
        if (quantidadeParaAtender > quantidadeRestante ){
            throw new IllegalArgumentException("A quantidade de envio não deve ser maior que a quantidade solictada");

        }

        Long saldoNoCentroOrigem = estoqueService.adicionaOuObtem(reserva.getMaterial(), reserva.getCentroCustoOrigem()).getSaldo();
        if (saldoNoCentroOrigem < quantidadeParaAtender) {
            throw new IllegalArgumentException("Não há saldo suficiente para atender a reserva.");
        }

        MovimentacaoMaterial movMaterial = new MovimentacaoMaterial();

        movMaterial.setMaterial(reserva.getMaterial());
        movMaterial.setCentroOrigem(reserva.getCentroCustoOrigem());
        movMaterial.setCentroDestino(reserva.getCentroCustoDestino());
        movMaterial.setQuantidadeMovimentada(quantidadeParaAtender);
        movMaterial.setTipo(TipoMovimentacao.TRANSFERENCIA);
        movMaterial.setObservacao("Atendimento da reserva: " + reserva.getId());

        movimentacaoMaterialRepository.save(movMaterial);
        estoqueService.debitaSaldo(reserva.getMaterial(), reserva.getCentroCustoOrigem(), quantidadeParaAtender);
        estoqueService.creditaSaldo(reserva.getMaterial(), reserva.getCentroCustoDestino(), quantidadeParaAtender);

        reserva.setQuantidadeAtendida(reserva.getQuantidadeAtendida() + quantidadeParaAtender);

        if (reserva.getQuantidadeAtendida().equals(reserva.getQuantidadeSolicitada()))
        {
            reserva.setStatus(StatusReserva.ATENDIDA);
            reserva.setDataAtendimento(new Date());
        }

        repository.save(reserva);


    }

    @Transactional
    public ReservaEstoque cancelaReservaAberta(Long id){
        ReservaEstoque reserva = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada: id = " + id));

        if (reserva.getStatus() != StatusReserva.ABERTA ){
            throw new IllegalArgumentException("Só é possível cancelar reservas abertas.");
        }

        reserva.setStatus(StatusReserva.CANCELADA);

        return repository.save(reserva);
    }


    public List<ReservaEstoque> listaTodasAsReservas() { return repository.findAll(); }
}
