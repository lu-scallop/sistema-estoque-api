package com.personal.crudapi.services;

import com.personal.crudapi.entities.CentroCusto;
import com.personal.crudapi.entities.EstoqueCentroCusto;
import com.personal.crudapi.entities.Material;
import com.personal.crudapi.repositories.CentroCustoRepository;
import com.personal.crudapi.repositories.EstoqueCentroCustoRepository;
import com.personal.crudapi.repositories.MaterialRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
@Import(EstoqueCentroCustoService.class)
public class EstoqueCentroCustoServiceTest {

    @Autowired
    EstoqueCentroCustoService service;

    @Autowired
    EstoqueCentroCustoRepository repository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    CentroCustoRepository centroCustoRepository;

    @Test
    @Transactional
    @Rollback
    void adicionaOuObtem_DeveAdicionarEstoque_QuandoEstoqueNaoExiste() {
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCusto);

        EstoqueCentroCusto estoque = service.adicionaOuObtem(material, centroCusto);
        estoque.setSaldo(50L);

        assertThat(estoque).isNotNull();
        assertThat(estoque.getCentroCusto().getCodigoCentroCusto()).isEqualTo("cc1");
        assertThat(estoque.getMaterial().getCodigoMaterial()).isEqualTo("22564");
        assertThat(estoque.getSaldo()).isEqualTo(50L);
    }

    @Test
    @Transactional
    @Rollback
    void adicionaOuObtem_DeveObterEstoque_QuandoEstoqueJaExiste(){
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
        estoque.setSaldo(0L);

        repository.save(estoque);

        Optional<EstoqueCentroCusto> obtemEstoque = repository.findByMaterialAndCentroCusto(material, centroCusto);
        assertThat(obtemEstoque).isPresent();
    }

    @Test
    @Transactional
    @Rollback
    void creditaSaldo_DeveSomarSaldoAoEstoque_QuandoEstoqueForExistente(){
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
        estoque.setSaldo(0L);

        repository.save(estoque);

        Long saldoParaSerCreditado = 50L;

        service.creditaSaldo(material, centroCusto, saldoParaSerCreditado);

        Optional<EstoqueCentroCusto> obtemEstoque = repository.findByMaterialAndCentroCusto(material, centroCusto);
        assertThat(obtemEstoque.get().getSaldo()).isEqualTo(50L);
    }

    @Test
    @Transactional
    @Rollback
    void creditaSaldo_DeveDispararExcecao_QuandoCreditoForMenorOuIgualZero(){
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
        estoque.setSaldo(0L);

        repository.save(estoque);

        Long saldoParaSerCreditado = -50L;

        assertThrows(IllegalArgumentException.class, () -> {
            service.creditaSaldo(material, centroCusto, saldoParaSerCreditado);
        });
    }

    @Test
    @Transactional
    @Rollback
    void debitaSaldo_DeveSubtrairSaldoDoEstoque_QuandoEstoqueForExistente(){
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

        repository.save(estoque);

        Long saldoParaSerDebitado = 50L;

        service.debitaSaldo(material, centroCusto, saldoParaSerDebitado);

        Optional<EstoqueCentroCusto> obtemEstoque = repository.findByMaterialAndCentroCusto(material, centroCusto);
        assertThat(obtemEstoque.get().getSaldo()).isEqualTo(50L);
    }

    @Test
    @Transactional
    @Rollback
    void debitaSaldo_DeveDispararExcecao_QuandoDebitoMenorIgualZero(){
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

        repository.save(estoque);

        Long saldoParaSerDebitado = -50L;

        assertThrows(IllegalArgumentException.class, () -> {
            service.debitaSaldo(material, centroCusto, saldoParaSerDebitado);
        });
    }

    @Test
    @Transactional
    @Rollback
    void debitaSaldo_DeveDispararExcecao_QuandoDebitoMaiorQueSaldo(){
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
        estoque.setSaldo(40L);

        repository.save(estoque);

        Long saldoParaSerDebitado = 50L;

        assertThrows(IllegalArgumentException.class, () -> {
            service.debitaSaldo(material, centroCusto, saldoParaSerDebitado);
        });
    }

    @Test
    @Transactional
    @Rollback
    void transfereSaldo_DeveTransferirSaldo_QuandoCentrosDiferentes(){
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

        EstoqueCentroCusto estoqueOrigem = new EstoqueCentroCusto();
        estoqueOrigem.setCentroCusto(centroCustoOrigem);
        estoqueOrigem.setMaterial(material);
        estoqueOrigem.setSaldo(100L);

        EstoqueCentroCusto estoqueDestino = new EstoqueCentroCusto();
        estoqueDestino.setCentroCusto(centroCustoDestino);
        estoqueDestino.setMaterial(material);
        estoqueDestino.setSaldo(20L);

        repository.save(estoqueOrigem);
        repository.save(estoqueDestino);

        Long quantidadeParaTransferir = 30L;

        service.transfereSaldo(material, centroCustoOrigem, centroCustoDestino, quantidadeParaTransferir);

        Optional<EstoqueCentroCusto> estoqueOrigemAtualizado = repository.findByMaterialAndCentroCusto(material, centroCustoOrigem);
        Optional<EstoqueCentroCusto> estoqueDestinoAtualizado = repository.findByMaterialAndCentroCusto(material, centroCustoDestino);

        assertThat(estoqueOrigemAtualizado.get().getSaldo()).isEqualTo(70L);
        assertThat(estoqueDestinoAtualizado.get().getSaldo()).isEqualTo(50L);
    }

    @Test
    @Transactional
    @Rollback
    void transfereSaldo_DeveDispararExcecao_QuandoOrigemDestinoIguais(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Acabamento");

        materialRepository.save(material);
        centroCustoRepository.save(centroCusto);

        EstoqueCentroCusto estoque = new EstoqueCentroCusto();
        estoque.setCentroCusto(centroCusto);
        estoque.setMaterial(material);
        estoque.setSaldo(100L);

        repository.save(estoque);

        Long quantidadeParaTransferir = 30L;

        assertThrows(IllegalArgumentException.class, () -> {
            service.transfereSaldo(material, centroCusto, centroCusto, quantidadeParaTransferir);
        });
    }

    @Test
    @Transactional
    @Rollback
    void transfereSaldo_DeveDispararExcecao_QuandoQuantidadeInvalida(){
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

        EstoqueCentroCusto estoqueOrigem = new EstoqueCentroCusto();
        estoqueOrigem.setCentroCusto(centroCustoOrigem);
        estoqueOrigem.setMaterial(material);
        estoqueOrigem.setSaldo(100L);

        repository.save(estoqueOrigem);

        Long quantidadeInvalida = -10L;

        assertThrows(IllegalArgumentException.class, () -> {
            service.transfereSaldo(material, centroCustoOrigem, centroCustoDestino, quantidadeInvalida);
        });
    }

    @Test
    @Transactional
    @Rollback
    void transfereSaldo_DeveDispararExcecao_QuandoSaldoInsuficiente(){
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

        EstoqueCentroCusto estoqueOrigem = new EstoqueCentroCusto();
        estoqueOrigem.setCentroCusto(centroCustoOrigem);
        estoqueOrigem.setMaterial(material);
        estoqueOrigem.setSaldo(30L);

        repository.save(estoqueOrigem);

        Long quantidadeMaiorQueSaldo = 50L;

        assertThrows(IllegalArgumentException.class, () -> {
            service.transfereSaldo(material, centroCustoOrigem, centroCustoDestino, quantidadeMaiorQueSaldo);
        });
    }

    @Test
    @Transactional
    @Rollback
    void listaTodosOsEstoques_DeveRetornarTodosEstoques(){
        // Arrange - Criar alguns estoques
        Material material1 = new Material();
        material1.setCodigoMaterial("22564");
        material1.setNome("Peça T");
        material1.setTipo("Corpo");

        Material material2 = new Material();
        material2.setCodigoMaterial("22565");
        material2.setNome("Peça X");
        material2.setTipo("Tampa");

        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Montagem");

        materialRepository.save(material1);
        materialRepository.save(material2);
        centroCustoRepository.save(centroCusto);

        EstoqueCentroCusto estoque1 = new EstoqueCentroCusto();
        estoque1.setCentroCusto(centroCusto);
        estoque1.setMaterial(material1);
        estoque1.setSaldo(50L);

        EstoqueCentroCusto estoque2 = new EstoqueCentroCusto();
        estoque2.setCentroCusto(centroCusto);
        estoque2.setMaterial(material2);
        estoque2.setSaldo(30L);

        repository.save(estoque1);
        repository.save(estoque2);

        List<EstoqueCentroCusto> estoques = service.listaTodosOsEstoques();

        assertThat(estoques).isNotNull();
        assertThat(estoques.size()).isEqualTo(2);
        assertThat(estoques.get(0).getSaldo()).isEqualTo(50L);
        assertThat(estoques.get(1).getSaldo()).isEqualTo(30L);
    }
}
