package com.personal.crudapi.service;

import com.personal.crudapi.dto.requests.ReservaEstoqueRequestDTO;
import com.personal.crudapi.entity.CentroCusto;
import com.personal.crudapi.entity.EstoqueCentroCusto;
import com.personal.crudapi.entity.Material;
import com.personal.crudapi.entity.ReservaEstoque;
import com.personal.crudapi.enums.StatusReserva;
import com.personal.crudapi.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
@Import({ReservaEstoqueService.class, EstoqueCentroCustoService.class})
public class ReservaEstoqueServiceTest {

    @Autowired
    private ReservaEstoqueService service;

    @Autowired
    private ReservaEstoqueRepository repository;

    @Autowired
    private EstoqueCentroCustoService estoqueService;

    @Autowired
    private EstoqueCentroCustoRepository estoqueRepository;

    @Autowired
    private MovimentacaoMaterialRepository movimentacaoMaterialRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private CentroCustoRepository centroCustoRepository;

    @Test
    @Transactional
    @Rollback
    void criaReserva_DeveCriarReserva_QuandoDadosValidos(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCustoOrigem = new CentroCusto();
        centroCustoOrigem.setCodigoCentroCusto("cc1");
        centroCustoOrigem.setNome("Acabamento");

        CentroCusto centroCustoDestino = new CentroCusto();
        centroCustoDestino.setCodigoCentroCusto("cc2");
        centroCustoDestino.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCustoOrigem);
        centroCustoRepository.save(centroCustoDestino);

        ReservaEstoqueRequestDTO dto = new ReservaEstoqueRequestDTO();
        dto.setCodigoMaterial(material.getCodigoMaterial());
        dto.setCentroCustoOrigem(centroCustoOrigem.getCodigoCentroCusto());
        dto.setCentroCustoDestino(centroCustoDestino.getCodigoCentroCusto());
        dto.setQuantidade(50L);

        ReservaEstoque reserva = service.criaReserva(dto);

        assertThat(reserva).isNotNull();
        assertThat(reserva.getId()).isNotNull();
        assertThat(reserva.getMaterial().getCodigoMaterial()).isEqualTo("22564");
        assertThat(reserva.getCentroCustoOrigem().getCodigoCentroCusto()).isEqualTo("cc1");
        assertThat(reserva.getCentroCustoDestino().getCodigoCentroCusto()).isEqualTo("cc2");
        assertThat(reserva.getQuantidadeSolicitada()).isEqualTo(50L);
        assertThat(reserva.getStatus()).isEqualTo(StatusReserva.ABERTA);
    }

    @Test
    @Transactional
    @Rollback
    void criaReserva_DeveDispararExcecao_QuandoOrigemIgualDestino(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCusto);

        ReservaEstoqueRequestDTO dto = new ReservaEstoqueRequestDTO();
        dto.setCodigoMaterial(material.getCodigoMaterial());
        dto.setCentroCustoOrigem(centroCusto.getCodigoCentroCusto());
        dto.setCentroCustoDestino(centroCusto.getCodigoCentroCusto());

        assertThrows(IllegalArgumentException.class, () -> {
            service.criaReserva(dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void atendePedidoDeReserva_DeveAtenderParcialmente_QuandoQuantidadeMenorQueSolicitada(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCustoOrigem = new CentroCusto();
        centroCustoOrigem.setCodigoCentroCusto("cc1");
        centroCustoOrigem.setNome("Acabamento");

        CentroCusto centroCustoDestino = new CentroCusto();
        centroCustoDestino.setCodigoCentroCusto("cc2");
        centroCustoDestino.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCustoOrigem);
        centroCustoRepository.save(centroCustoDestino);

        EstoqueCentroCusto estoque = new EstoqueCentroCusto();
        estoque.setCentroCusto(centroCustoOrigem);
        estoque.setMaterial(material);
        estoque.setSaldo(100L);
        estoqueRepository.save(estoque);

        ReservaEstoque reserva = new ReservaEstoque();
        reserva.setMaterial(material);
        reserva.setCentroCustoOrigem(centroCustoOrigem);
        reserva.setCentroCustoDestino(centroCustoDestino);
        reserva.setQuantidadeSolicitada(80L);
        reserva.setQuantidadeAtendida(0L);
        reserva.setStatus(StatusReserva.ABERTA);
        ReservaEstoque reservaSalva = repository.save(reserva);

        service.atendePedidoDeReserva(reservaSalva.getId(), 30L);

        Optional<ReservaEstoque> reservaAtualizada = repository.findById(reservaSalva.getId());
        assertThat(reservaAtualizada.get().getQuantidadeAtendida()).isEqualTo(30L);
        assertThat(reservaAtualizada.get().getStatus()).isEqualTo(StatusReserva.ABERTA);

        Optional<EstoqueCentroCusto> estoqueOrigemAtualizado = estoqueRepository.findByMaterialAndCentroCusto(material, centroCustoOrigem);
        Optional<EstoqueCentroCusto> estoqueDestinoAtualizado = estoqueRepository.findByMaterialAndCentroCusto(material, centroCustoDestino);

        assertThat(estoqueOrigemAtualizado.get().getSaldo()).isEqualTo(70L);
        assertThat(estoqueDestinoAtualizado.get().getSaldo()).isEqualTo(30L);
    }

    @Test
    @Transactional
    @Rollback
    void atendePedidoDeReserva_DeveAtenderTotalmente_QuandoQuantidadeIgualSolicitada(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCustoOrigem = new CentroCusto();
        centroCustoOrigem.setCodigoCentroCusto("cc1");
        centroCustoOrigem.setNome("Acabamento");

        CentroCusto centroCustoDestino = new CentroCusto();
        centroCustoDestino.setCodigoCentroCusto("cc2");
        centroCustoDestino.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCustoOrigem);
        centroCustoRepository.save(centroCustoDestino);

        EstoqueCentroCusto estoque = new EstoqueCentroCusto();
        estoque.setCentroCusto(centroCustoOrigem);
        estoque.setMaterial(material);
        estoque.setSaldo(100L);
        estoqueRepository.save(estoque);

        ReservaEstoque reserva = new ReservaEstoque();
        reserva.setMaterial(material);
        reserva.setCentroCustoOrigem(centroCustoOrigem);
        reserva.setCentroCustoDestino(centroCustoDestino);
        reserva.setQuantidadeSolicitada(50L);
        reserva.setQuantidadeAtendida(0L);
        reserva.setStatus(StatusReserva.ABERTA);
        ReservaEstoque reservaSalva = repository.save(reserva);

        service.atendePedidoDeReserva(reservaSalva.getId(), 50L);

        Optional<ReservaEstoque> reservaAtualizada = repository.findById(reservaSalva.getId());
        assertThat(reservaAtualizada.get().getQuantidadeAtendida()).isEqualTo(50L);
        assertThat(reservaAtualizada.get().getStatus()).isEqualTo(StatusReserva.ATENDIDA);
        assertThat(reservaAtualizada.get().getDataAtendimento()).isNotNull();
    }

    @Test
    @Transactional
    @Rollback
    void atendePedidoDeReserva_DeveDispararExcecao_QuandoQuantidadeInvalida(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCustoOrigem = new CentroCusto();
        centroCustoOrigem.setCodigoCentroCusto("cc1");
        centroCustoOrigem.setNome("Acabamento");

        CentroCusto centroCustoDestino = new CentroCusto();
        centroCustoDestino.setCodigoCentroCusto("cc2");
        centroCustoDestino.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCustoOrigem);
        centroCustoRepository.save(centroCustoDestino);

        ReservaEstoque reserva = new ReservaEstoque();
        reserva.setMaterial(material);
        reserva.setCentroCustoOrigem(centroCustoOrigem);
        reserva.setCentroCustoDestino(centroCustoDestino);
        reserva.setQuantidadeSolicitada(50L);
        reserva.setQuantidadeAtendida(0L);
        reserva.setStatus(StatusReserva.ABERTA);
        ReservaEstoque reservaSalva = repository.save(reserva);

        assertThrows(IllegalArgumentException.class, () -> {
            service.atendePedidoDeReserva(reservaSalva.getId(), -10L);
        });
    }

    @Test
    @Transactional
    @Rollback
    void atendePedidoDeReserva_DeveDispararExcecao_QuandoSaldoInsuficiente(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCustoOrigem = new CentroCusto();
        centroCustoOrigem.setCodigoCentroCusto("cc1");
        centroCustoOrigem.setNome("Acabamento");

        CentroCusto centroCustoDestino = new CentroCusto();
        centroCustoDestino.setCodigoCentroCusto("cc2");
        centroCustoDestino.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCustoOrigem);
        centroCustoRepository.save(centroCustoDestino);

        EstoqueCentroCusto estoque = new EstoqueCentroCusto();
        estoque.setCentroCusto(centroCustoOrigem);
        estoque.setMaterial(material);
        estoque.setSaldo(20L);
        estoqueRepository.save(estoque);

        ReservaEstoque reserva = new ReservaEstoque();
        reserva.setMaterial(material);
        reserva.setCentroCustoOrigem(centroCustoOrigem);
        reserva.setCentroCustoDestino(centroCustoDestino);
        reserva.setQuantidadeSolicitada(50L);
        reserva.setQuantidadeAtendida(0L);
        reserva.setStatus(StatusReserva.ABERTA);
        ReservaEstoque reservaSalva = repository.save(reserva);

        assertThrows(IllegalArgumentException.class, () -> {
            service.atendePedidoDeReserva(reservaSalva.getId(), 30L);
        });
    }

    @Test
    @Transactional
    @Rollback
    void atendePedidoDeReserva_DeveDispararExcecao_QuandoReservaJaAtendida(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCustoOrigem = new CentroCusto();
        centroCustoOrigem.setCodigoCentroCusto("cc1");
        centroCustoOrigem.setNome("Acabamento");

        CentroCusto centroCustoDestino = new CentroCusto();
        centroCustoDestino.setCodigoCentroCusto("cc2");
        centroCustoDestino.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCustoOrigem);
        centroCustoRepository.save(centroCustoDestino);

        ReservaEstoque reserva = new ReservaEstoque();
        reserva.setMaterial(material);
        reserva.setCentroCustoOrigem(centroCustoOrigem);
        reserva.setCentroCustoDestino(centroCustoDestino);
        reserva.setQuantidadeSolicitada(50L);
        reserva.setQuantidadeAtendida(50L);
        reserva.setStatus(StatusReserva.ATENDIDA);
        ReservaEstoque reservaSalva = repository.save(reserva);

        assertThrows(IllegalArgumentException.class, () -> {
            service.atendePedidoDeReserva(reservaSalva.getId(), 10L);
        });
    }

    @Test
    @Transactional
    @Rollback
    void atendePedidoDeReserva_DeveDispararExcecao_QuandoQuantidadeMaiorQueRestante(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCustoOrigem = new CentroCusto();
        centroCustoOrigem.setCodigoCentroCusto("cc1");
        centroCustoOrigem.setNome("Acabamento");

        CentroCusto centroCustoDestino = new CentroCusto();
        centroCustoDestino.setCodigoCentroCusto("cc2");
        centroCustoDestino.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCustoOrigem);
        centroCustoRepository.save(centroCustoDestino);

        EstoqueCentroCusto estoque = new EstoqueCentroCusto();
        estoque.setCentroCusto(centroCustoOrigem);
        estoque.setMaterial(material);
        estoque.setSaldo(100L);
        estoqueRepository.save(estoque);

        ReservaEstoque reserva = new ReservaEstoque();
        reserva.setMaterial(material);
        reserva.setCentroCustoOrigem(centroCustoOrigem);
        reserva.setCentroCustoDestino(centroCustoDestino);
        reserva.setQuantidadeSolicitada(50L);
        reserva.setQuantidadeAtendida(30L);
        reserva.setStatus(StatusReserva.ABERTA);
        ReservaEstoque reservaSalva = repository.save(reserva);

        assertThrows(IllegalArgumentException.class, () -> {
            service.atendePedidoDeReserva(reservaSalva.getId(), 30L);
        });
    }

    @Test
    @Transactional
    @Rollback
    void cancelaReservaAberta_DeveCancelarReserva_QuandoReservaAberta(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCustoOrigem = new CentroCusto();
        centroCustoOrigem.setCodigoCentroCusto("cc1");
        centroCustoOrigem.setNome("Acabamento");

        CentroCusto centroCustoDestino = new CentroCusto();
        centroCustoDestino.setCodigoCentroCusto("cc2");
        centroCustoDestino.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCustoOrigem);
        centroCustoRepository.save(centroCustoDestino);

        ReservaEstoque reserva = new ReservaEstoque();
        reserva.setMaterial(material);
        reserva.setCentroCustoOrigem(centroCustoOrigem);
        reserva.setCentroCustoDestino(centroCustoDestino);
        reserva.setQuantidadeSolicitada(50L);
        reserva.setQuantidadeAtendida(0L);
        reserva.setStatus(StatusReserva.ABERTA);
        ReservaEstoque reservaSalva = repository.save(reserva);

        ReservaEstoque reservaCancelada = service.cancelaReservaAberta(reservaSalva.getId());

        assertThat(reservaCancelada.getStatus()).isEqualTo(StatusReserva.CANCELADA);
    }

    @Test
    @Transactional
    @Rollback
    void cancelaReservaAberta_DeveDispararExcecao_QuandoReservaNaoAberta(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCustoOrigem = new CentroCusto();
        centroCustoOrigem.setCodigoCentroCusto("cc1");
        centroCustoOrigem.setNome("Acabamento");

        CentroCusto centroCustoDestino = new CentroCusto();
        centroCustoDestino.setCodigoCentroCusto("cc2");
        centroCustoDestino.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCustoOrigem);
        centroCustoRepository.save(centroCustoDestino);

        ReservaEstoque reserva = new ReservaEstoque();
        reserva.setMaterial(material);
        reserva.setCentroCustoOrigem(centroCustoOrigem);
        reserva.setCentroCustoDestino(centroCustoDestino);
        reserva.setQuantidadeSolicitada(50L);
        reserva.setQuantidadeAtendida(50L);
        reserva.setStatus(StatusReserva.ATENDIDA);
        ReservaEstoque reservaSalva = repository.save(reserva);

        assertThrows(IllegalArgumentException.class, () -> {
            service.cancelaReservaAberta(reservaSalva.getId());
        });
    }
}