package com.personal.crudapi.service;

import com.personal.crudapi.dto.ReservaEstoqueDTO;
import com.personal.crudapi.entity.CentroCusto;
import com.personal.crudapi.entity.Material;
import com.personal.crudapi.entity.ReservaEstoque;
import com.personal.crudapi.enums.StatusReserva;
import com.personal.crudapi.repository.ReservaEstoqueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaEstoqueService {

    @Autowired
    private ReservaEstoqueRepository repository;
    @Autowired
    private EstoqueCentroCustoService estoqueService;

    @Transactional
    public ReservaEstoque adicionaReserva(ReservaEstoqueDTO dto){
        ReservaEstoque reserva = new ReservaEstoque();

        if (dto.getMaterial() == null)
            throw new IllegalArgumentException("Material não encontrado.");
        if (dto.getCentroCustoOrigem() == null)
            throw new IllegalArgumentException("Centro de origem não encontrado.");
        if (dto.getCentroCustoDestino() == null)
            throw new IllegalArgumentException("Centro de destino não encontrado.");
        if (dto.getCentroCustoOrigem().equals(dto.getCentroCustoDestino()))
            throw new IllegalArgumentException("Origem e Destino NÃO devem ser iguais.");

        reserva.setMaterial(dto.getMaterial());
        reserva.setCentroCustoOrigem(dto.getCentroCustoOrigem());
        reserva.setCentroCustoDestino(dto.getCentroCustoDestino());
        reserva.setQuantidadeSolicitada(dto.getQuantidade());
        reserva.setStatus(StatusReserva.ABERTA);

        return repository.save(reserva);

    }

    @Transactional
    public Long salvoDisponivelParaReserva(Material material, CentroCusto origem)
    {
        Long saldo = estoqueService.adicionaOuObtem(material, origem).getSaldo();
        Long reservado = repository.reservadoEmAberto(material, origem, List.of(StatusReserva.APROVADA, StatusReserva.ABERTA));
        return saldo - reservado;
    }


    public List<ReservaEstoque> listaTodasAsReservas() { return repository.findAll(); }
}
