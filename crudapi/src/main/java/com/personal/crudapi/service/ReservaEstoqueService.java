package com.personal.crudapi.service;

import com.personal.crudapi.dto.ReservaEstoqueDTO;
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

    @Transactional
    public ReservaEstoque adicionaReserva(ReservaEstoqueDTO dto){
        ReservaEstoque reserva = new ReservaEstoque();
        reserva.setMaterial(dto.getMaterial());
        reserva.setCentroCustoOrigem(dto.getCentroCustoOrigem());
        reserva.setCentroCustoDestino(dto.getCentroCustoDestino());
        reserva.setQuantidadeSolicitada(dto.getQuantidade());
        reserva.setStatus(StatusReserva.ABERTA);

        return repository.save(reserva);

    }

    public List<ReservaEstoque> listaTodasAsReservas() { return repository.findAll(); }
}
