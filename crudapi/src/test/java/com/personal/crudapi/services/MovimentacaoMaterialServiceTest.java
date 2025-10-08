package com.personal.crudapi.services;

import com.personal.crudapi.dtos.requests.EntradaMaterialRequestDTO;
import com.personal.crudapi.dtos.requests.SaidaMaterialRequestDTO;
import com.personal.crudapi.dtos.requests.TransfereMaterialRequestDTO;
import com.personal.crudapi.entities.CentroCusto;
import com.personal.crudapi.entities.EstoqueCentroCusto;
import com.personal.crudapi.entities.Material;
import com.personal.crudapi.entities.MovimentacaoMaterial;
import com.personal.crudapi.enums.TipoMovimentacao;
import com.personal.crudapi.repositories.CentroCustoRepository;
import com.personal.crudapi.repositories.EstoqueCentroCustoRepository;
import com.personal.crudapi.repositories.MaterialRepository;
import com.personal.crudapi.repositories.MovimentacaoMaterialRepository;
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
@Import({MovimentacaoMaterialService.class, EstoqueCentroCustoService.class})
public class MovimentacaoMaterialServiceTest {

    @Autowired
    private MovimentacaoMaterialService service;

    @Autowired
    private MovimentacaoMaterialRepository repository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private CentroCustoRepository centroCustoRepository;

    @Autowired
    private EstoqueCentroCustoRepository estoqueRepository;

    @Test
    @Transactional
    @Rollback
    void transfereMaterial_DeveTransferirMaterial_QuandoTransferenciaValida(){
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
        estoqueOrigem.setSaldo(50L);

        estoqueRepository.save(estoqueOrigem);

        TransfereMaterialRequestDTO dto = new TransfereMaterialRequestDTO();
        dto.setCodigoMaterial(material.getCodigoMaterial());
        dto.setCodigoCentroOrigem(centroCustoOrigem.getCodigoCentroCusto());
        dto.setCodigoCentroDestino(centroCustoDestino.getCodigoCentroCusto());
        dto.setQuantidadeMovimentada(30L);

        MovimentacaoMaterial movimentacao = service.transfereMaterial(dto);

        assertThat(movimentacao).isNotNull();
        assertThat(movimentacao.getMaterial().getCodigoMaterial()).isEqualTo("22564");
        assertThat(movimentacao.getCentroOrigem().getCodigoCentroCusto()).isEqualTo("cc1");
        assertThat(movimentacao.getCentroDestino().getCodigoCentroCusto()).isEqualTo("cc2");
        assertThat(movimentacao.getQuantidadeMovimentada()).isEqualTo(30L);
        assertThat(movimentacao.getTipo()).isEqualTo(TipoMovimentacao.TRANSFERENCIA);

        Optional<EstoqueCentroCusto> estoqueOrigemAtualizado = estoqueRepository.findByMaterialAndCentroCusto(material, centroCustoOrigem);
        Optional<EstoqueCentroCusto> estoqueDestinoAtualizado = estoqueRepository.findByMaterialAndCentroCusto(material, centroCustoDestino);

        assertThat(estoqueOrigemAtualizado.get().getSaldo()).isEqualTo(20L);
        assertThat(estoqueDestinoAtualizado.get().getSaldo()).isEqualTo(30L);
    }

    @Test
    @Transactional
    @Rollback
    void transfereMaterial_DeveDispararExcecao_QuandoQuantidadeInvalida(){
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

        TransfereMaterialRequestDTO dto = new TransfereMaterialRequestDTO();
        dto.setCodigoMaterial(material.getCodigoMaterial());
        dto.setCodigoCentroOrigem(centroCustoOrigem.getCodigoCentroCusto());
        dto.setCodigoCentroDestino(centroCustoDestino.getCodigoCentroCusto());
        dto.setQuantidadeMovimentada(-10L);

        assertThrows(IllegalArgumentException.class, () -> {
            service.transfereMaterial(dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void transfereMaterial_DeveDispararExcecao_QuandoCentroOrigemIgualDestino(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCusto);

        TransfereMaterialRequestDTO dto = new TransfereMaterialRequestDTO();
        dto.setCodigoMaterial(material.getCodigoMaterial());
        dto.setCodigoCentroOrigem(centroCusto.getCodigoCentroCusto());
        dto.setCodigoCentroDestino(centroCusto.getCodigoCentroCusto());
        dto.setQuantidadeMovimentada(10L);

        assertThrows(IllegalArgumentException.class, () -> {
            service.transfereMaterial(dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void entraMaterial_DeveAdicionarQuantidadeNoCentroCusto_QuandoCentroExistir(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Montagem");

        EstoqueCentroCusto estoqueOrigem = new EstoqueCentroCusto();
        estoqueOrigem.setCentroCusto(centroCusto);
        estoqueOrigem.setMaterial(material);
        estoqueOrigem.setSaldo(50L);

        materialRepository.save(material);
        centroCustoRepository.save(centroCusto);
        estoqueRepository.save(estoqueOrigem);

        EntradaMaterialRequestDTO dto = new EntradaMaterialRequestDTO();
        dto.setCodigoMaterial(material.getCodigoMaterial());
        dto.setCodigoCentroDestino(centroCusto.getCodigoCentroCusto());
        dto.setQuantidadeMovimentada(30L);

        MovimentacaoMaterial movimentacao = service.entraMaterial(dto);

        assertThat(movimentacao).isNotNull();
        assertThat(movimentacao.getMaterial().getCodigoMaterial()).isEqualTo("22564");
        assertThat(movimentacao.getCentroDestino().getCodigoCentroCusto()).isEqualTo("cc1");
        assertThat(movimentacao.getQuantidadeMovimentada()).isEqualTo(30L);
        assertThat(movimentacao.getTipo()).isEqualTo(TipoMovimentacao.ENTRADA);

        Optional<EstoqueCentroCusto> estoqueDestinoAtualizado = estoqueRepository.findByMaterialAndCentroCusto(material, centroCusto);

        assertThat(estoqueDestinoAtualizado.get().getSaldo()).isEqualTo(80L);
    }

    @Test
    @Transactional
    @Rollback
    void entraMaterial_DeveDispararExcecao_QuandoQuantidadeInvalida(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCustoDestino = new CentroCusto();
        centroCustoDestino.setCodigoCentroCusto("cc2");
        centroCustoDestino.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCustoDestino);

        EntradaMaterialRequestDTO dto = new EntradaMaterialRequestDTO();
        dto.setCodigoMaterial(material.getCodigoMaterial());
        dto.setCodigoCentroDestino(centroCustoDestino.getCodigoCentroCusto());
        dto.setQuantidadeMovimentada(0L);

        assertThrows(IllegalArgumentException.class, () -> {
            service.entraMaterial(dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void entraMaterial_DeveDispararExcecao_QuandoQuantidadeNegativa(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCustoDestino = new CentroCusto();
        centroCustoDestino.setCodigoCentroCusto("cc2");
        centroCustoDestino.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCustoDestino);

        EntradaMaterialRequestDTO dto = new EntradaMaterialRequestDTO();
        dto.setCodigoMaterial(material.getCodigoMaterial());
        dto.setCodigoCentroDestino(centroCustoDestino.getCodigoCentroCusto());
        dto.setQuantidadeMovimentada(-20L);

        assertThrows(IllegalArgumentException.class, () -> {
            service.entraMaterial(dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void saiMaterial_DeveSairMaterial_QuandoSaidaValida(){
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

        SaidaMaterialRequestDTO dto = new SaidaMaterialRequestDTO();
        dto.setCodigoMaterial(material.getCodigoMaterial());
        dto.setCodigoCentroOrigem(centroCusto.getCodigoCentroCusto());
        dto.setQuantidadeMovimentada(30L);

        MovimentacaoMaterial movimentacao = service.saiMaterial(dto);

        assertThat(movimentacao).isNotNull();
        assertThat(movimentacao.getMaterial().getCodigoMaterial()).isEqualTo("22564");
        assertThat(movimentacao.getCentroOrigem().getCodigoCentroCusto()).isEqualTo("cc1");
        assertThat(movimentacao.getQuantidadeMovimentada()).isEqualTo(30L);
        assertThat(movimentacao.getTipo()).isEqualTo(TipoMovimentacao.SAIDA);

        Optional<EstoqueCentroCusto> estoqueAtualizado = estoqueRepository.findByMaterialAndCentroCusto(material, centroCusto);
        assertThat(estoqueAtualizado.get().getSaldo()).isEqualTo(70L);
    }

    @Test
    @Transactional
    @Rollback
    void saiMaterial_DeveDispararExcecao_QuandoQuantidadeInvalida(){
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Montagem");

        materialRepository.save(material);
        centroCustoRepository.save(centroCusto);

        SaidaMaterialRequestDTO dto = new SaidaMaterialRequestDTO();
        dto.setCodigoMaterial(material.getCodigoMaterial());
        dto.setCodigoCentroOrigem(centroCusto.getCodigoCentroCusto());
        dto.setQuantidadeMovimentada(-10L);

        assertThrows(IllegalArgumentException.class, () -> {
            service.saiMaterial(dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void saiMaterial_DeveDispararExcecao_QuandoSaldoInsuficiente(){
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

        SaidaMaterialRequestDTO dto = new SaidaMaterialRequestDTO();
        dto.setCodigoMaterial(material.getCodigoMaterial());
        dto.setCodigoCentroOrigem(centroCusto.getCodigoCentroCusto());
        dto.setQuantidadeMovimentada(50L);

        assertThrows(IllegalArgumentException.class, () -> {
            service.saiMaterial(dto);
        });
    }


}
