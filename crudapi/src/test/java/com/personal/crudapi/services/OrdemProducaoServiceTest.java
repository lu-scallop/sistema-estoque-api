package com.personal.crudapi.services;

import com.personal.crudapi.dtos.requests.OrdemProducaoRequestDTO;
import com.personal.crudapi.entities.CentroCusto;
import com.personal.crudapi.entities.EstoqueCentroCusto;
import com.personal.crudapi.entities.Material;
import com.personal.crudapi.entities.OrdemProducao;
import com.personal.crudapi.enums.StatusOrdem;
import com.personal.crudapi.repositories.*;
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
@Import({OrdemProducaoService.class, EstoqueCentroCustoService.class})
public class OrdemProducaoServiceTest {

    @Autowired
    private OrdemProducaoService service;

    @Autowired
    private OrdemProducaoRepository repository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private CentroCustoRepository centroCustoRepository;

    @Autowired
    private EstoqueCentroCustoRepository estoqueRepository;

    @Autowired
    private MovimentacaoMaterialRepository movimentacaoRepository;

    @Test
    @Transactional
    @Rollback
    void criaOrdemDeProducao_DeveCriarOrdem_QuandoDadosValidos(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCusto);

        OrdemProducaoRequestDTO dto = new OrdemProducaoRequestDTO();
        dto.setCodigoProducao("OP-001");
        dto.setCodigoMaterial(material.getCodigoMaterial());
        dto.setCodigoCentroCusto(centroCusto.getCodigoCentroCusto());
        dto.setQuantidadePlanejada(100L);

        OrdemProducao op = service.criaOrderDeProducao(dto);

        assertThat(op).isNotNull();
        assertThat(op.getId()).isNotNull();
        assertThat(op.getCodigoProducao()).isEqualTo("OP-001");
        assertThat(op.getMaterial().getCodigoMaterial()).isEqualTo("22564");
        assertThat(op.getCentroCusto().getCodigoCentroCusto()).isEqualTo("cc1");
        assertThat(op.getQuantidadePlanejada()).isEqualTo(100L);
        assertThat(op.getQuantidadeConcluida()).isEqualTo(0L);
        assertThat(op.getStatus()).isEqualTo(StatusOrdem.LIBERADA);
        assertThat(op.getDataAbertura()).isNotNull();
    }

    @Test
    @Transactional
    @Rollback
    void criaOrdemDeProducao_DeveDispararExcecao_QuandoQuantidadeMenorQue1(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCusto);

        OrdemProducaoRequestDTO dto = new OrdemProducaoRequestDTO();
        dto.setCodigoProducao("OP-001");
        dto.setCodigoMaterial(material.getCodigoMaterial());
        dto.setCodigoCentroCusto(centroCusto.getCodigoCentroCusto());
        dto.setQuantidadePlanejada(0L);
        assertThrows(IllegalArgumentException.class, () -> {
            service.criaOrderDeProducao(dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void criaOrdemDeProducao_DeveDispararExcecao_QuandoMaterialNaoEncontrado(){
        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Montagem");

        centroCustoRepository.save(centroCusto);

        OrdemProducaoRequestDTO dto = new OrdemProducaoRequestDTO();
        dto.setCodigoProducao("OP-001");
        dto.setCodigoMaterial("MATERIAL_INEXISTENTE");
        dto.setCodigoCentroCusto(centroCusto.getCodigoCentroCusto());
        dto.setQuantidadePlanejada(100L);

        assertThrows(IllegalArgumentException.class, () -> {
            service.criaOrderDeProducao(dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void criaOrdemDeProducao_DeveDispararExcecao_QuandoCentroCustoNaoEncontrado(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        materialRepository.save(material);

        OrdemProducaoRequestDTO dto = new OrdemProducaoRequestDTO();
        dto.setCodigoProducao("OP-001");
        dto.setCodigoMaterial(material.getCodigoMaterial());
        dto.setCodigoCentroCusto("CC_INEXISTENTE");
        dto.setQuantidadePlanejada(100L);

        assertThrows(IllegalArgumentException.class, () -> {
            service.criaOrderDeProducao(dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void apontaProducao_DeveApontarParcialmente_QuandoQuantidadeMenorQuePlanejada(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCusto);

        EstoqueCentroCusto estoque = new EstoqueCentroCusto();
        estoque.setCentroCusto(centroCusto);
        estoque.setMaterial(material);
        estoque.setSaldo(100L);
        estoqueRepository.save(estoque);

        OrdemProducao op = new OrdemProducao();
        op.setCodigoProducao("OP-001");
        op.setMaterial(material);
        op.setCentroCusto(centroCusto);
        op.setQuantidadePlanejada(100L);
        op.setQuantidadeConcluida(0L);
        op.setStatus(StatusOrdem.LIBERADA);
        OrdemProducao opSalva = repository.save(op);

        service.apontaProducao(opSalva.getId(), 30L);

        Optional<OrdemProducao> opAtualizada = repository.findById(opSalva.getId());
        assertThat(opAtualizada.get().getQuantidadeConcluida()).isEqualTo(30L);
        assertThat(opAtualizada.get().getStatus()).isEqualTo(StatusOrdem.EM_ANDAMENTO);
        assertThat(opAtualizada.get().getDataFechamento()).isNull();

        Optional<EstoqueCentroCusto> estoqueAtualizado = estoqueRepository.findByMaterialAndCentroCusto(material, centroCusto);
        assertThat(estoqueAtualizado.get().getSaldo()).isEqualTo(70L);
    }

    @Test
    @Transactional
    @Rollback
    void apontaProducao_DeveApontarTotalmente_QuandoQuantidadeIgualPlanejada(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCusto);

        EstoqueCentroCusto estoque = new EstoqueCentroCusto();
        estoque.setCentroCusto(centroCusto);
        estoque.setMaterial(material);
        estoque.setSaldo(100L);
        estoqueRepository.save(estoque);

        OrdemProducao op = new OrdemProducao();
        op.setCodigoProducao("OP-001");
        op.setMaterial(material);
        op.setCentroCusto(centroCusto);
        op.setQuantidadePlanejada(50L);
        op.setQuantidadeConcluida(0L);
        op.setStatus(StatusOrdem.LIBERADA);
        OrdemProducao opSalva = repository.save(op);

        service.apontaProducao(opSalva.getId(), 50L);

        Optional<OrdemProducao> opAtualizada = repository.findById(opSalva.getId());
        assertThat(opAtualizada.get().getQuantidadeConcluida()).isEqualTo(50L);
        assertThat(opAtualizada.get().getStatus()).isEqualTo(StatusOrdem.CONCLUIDA);
        assertThat(opAtualizada.get().getDataFechamento()).isNotNull();
    }

    @Test
    @Transactional
    @Rollback
    void apontaProducao_DeveDispararExcecao_QuandoQuantidadeInvalida(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCusto);

        OrdemProducao op = new OrdemProducao();
        op.setCodigoProducao("OP-001");
        op.setMaterial(material);
        op.setCentroCusto(centroCusto);
        op.setQuantidadePlanejada(100L);
        op.setQuantidadeConcluida(0L);
        op.setStatus(StatusOrdem.LIBERADA);
        OrdemProducao opSalva = repository.save(op);

        assertThrows(IllegalArgumentException.class, () -> {
            service.apontaProducao(opSalva.getId(), 0L);
        });
    }

    @Test
    @Transactional
    @Rollback
    void apontaProducao_DeveDispararExcecao_QuandoOrdemJaConcluida(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCusto);

        OrdemProducao op = new OrdemProducao();
        op.setCodigoProducao("OP-001");
        op.setMaterial(material);
        op.setCentroCusto(centroCusto);
        op.setQuantidadePlanejada(100L);
        op.setQuantidadeConcluida(100L);
        op.setStatus(StatusOrdem.CONCLUIDA);
        OrdemProducao opSalva = repository.save(op);

        assertThrows(IllegalArgumentException.class, () -> {
            service.apontaProducao(opSalva.getId(), 10L);
        });
    }

    @Test
    @Transactional
    @Rollback
    void apontaProducao_DeveDispararExcecao_QuandoSaldoInsuficiente(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCusto);

        EstoqueCentroCusto estoque = new EstoqueCentroCusto();
        estoque.setCentroCusto(centroCusto);
        estoque.setMaterial(material);
        estoque.setSaldo(20L);
        estoqueRepository.save(estoque);

        OrdemProducao op = new OrdemProducao();
        op.setCodigoProducao("OP-001");
        op.setMaterial(material);
        op.setCentroCusto(centroCusto);
        op.setQuantidadePlanejada(100L);
        op.setQuantidadeConcluida(0L);
        op.setStatus(StatusOrdem.LIBERADA);
        OrdemProducao opSalva = repository.save(op);

        assertThrows(IllegalArgumentException.class, () -> {
            service.apontaProducao(opSalva.getId(), 50L);
        });
    }

    @Test
    @Transactional
    @Rollback
    void apontaProducao_DeveDispararExcecao_QuandoExcedeQuantidadePlanejada(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCusto);

        EstoqueCentroCusto estoque = new EstoqueCentroCusto();
        estoque.setCentroCusto(centroCusto);
        estoque.setMaterial(material);
        estoque.setSaldo(200L);
        estoqueRepository.save(estoque);

        OrdemProducao op = new OrdemProducao();
        op.setCodigoProducao("OP-001");
        op.setMaterial(material);
        op.setCentroCusto(centroCusto);
        op.setQuantidadePlanejada(50L);
        op.setQuantidadeConcluida(30L);
        op.setStatus(StatusOrdem.EM_ANDAMENTO);
        OrdemProducao opSalva = repository.save(op);

        assertThrows(IllegalArgumentException.class, () -> {
            service.apontaProducao(opSalva.getId(), 30L);
        });
    }

    @Test
    @Transactional
    @Rollback
    void deletarOrdemProducao_DeveDeletarOrdem_QuandoNaoIniciada(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCusto);

        OrdemProducao op = new OrdemProducao();
        op.setCodigoProducao("OP-001");
        op.setMaterial(material);
        op.setCentroCusto(centroCusto);
        op.setQuantidadePlanejada(100L);
        op.setQuantidadeConcluida(0L);
        op.setStatus(StatusOrdem.LIBERADA);
        OrdemProducao opSalva = repository.save(op);

        service.deletarOrdemProducao(opSalva.getId());

        Optional<OrdemProducao> opDeletada = repository.findById(opSalva.getId());
        assertThat(opDeletada).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void deletarOrdemProducao_DeveDispararExcecao_QuandoJaIniciada(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCusto);

        OrdemProducao op = new OrdemProducao();
        op.setCodigoProducao("OP-001");
        op.setMaterial(material);
        op.setCentroCusto(centroCusto);
        op.setQuantidadePlanejada(100L);
        op.setQuantidadeConcluida(30L);
        op.setStatus(StatusOrdem.EM_ANDAMENTO);
        OrdemProducao opSalva = repository.save(op);

        assertThrows(IllegalArgumentException.class, () -> {
            service.deletarOrdemProducao(opSalva.getId());
        });
    }

    @Test
    @Transactional
    @Rollback
    void deletarOrdemProducao_DeveDispararExcecao_QuandoOrdemNaoEncontrada(){
        Long idInexistente = 99999L;

        assertThrows(IllegalArgumentException.class, () -> {
            service.deletarOrdemProducao(idInexistente);
        });
    }
}