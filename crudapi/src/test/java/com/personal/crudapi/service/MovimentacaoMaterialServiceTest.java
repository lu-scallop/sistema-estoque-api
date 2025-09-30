package com.personal.crudapi.service;

import com.personal.crudapi.dto.requests.TransfereMaterialRequestDTO;
import com.personal.crudapi.entity.CentroCusto;
import com.personal.crudapi.entity.EstoqueCentroCusto;
import com.personal.crudapi.entity.Material;
import com.personal.crudapi.entity.MovimentacaoMaterial;
import com.personal.crudapi.enums.TipoMovimentacao;
import com.personal.crudapi.repository.CentroCustoRepository;
import com.personal.crudapi.repository.EstoqueCentroCustoRepository;
import com.personal.crudapi.repository.MaterialRepository;
import com.personal.crudapi.repository.MovimentacaoMaterialRepository;
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
}
