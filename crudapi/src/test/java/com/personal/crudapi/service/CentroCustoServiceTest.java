package com.personal.crudapi.service;

import com.personal.crudapi.dto.requests.CentroCustoRequestDTO;
import com.personal.crudapi.entity.CentroCusto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import com.personal.crudapi.repository.CentroCustoRepository;
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
@Import(CentroCustoService.class)
public class CentroCustoServiceTest {

    @Autowired
    CentroCustoService service;

    @Autowired
    CentroCustoRepository repository;

    @Test
    @Transactional
    @Rollback
    void adicionaCentroDeCusto_DeveAdicionarCentroDeCusto_QuandoCentroNaoExiste() {
        var request = new CentroCustoRequestDTO();
        request.setCodigoCentroCusto("cc1");
        request.setNome("Montagem");

        CentroCusto centroSalvo = service.adicionaCentroDeCusto(request);

        assertThat(centroSalvo).isNotNull();
        assertThat(centroSalvo.getId()).isNotNull();
        assertThat(centroSalvo.getCodigoCentroCusto()).isEqualTo("cc1");
        assertThat(centroSalvo.getNome()).isEqualTo("Montagem");

        Optional<CentroCusto> noBanco = repository.findByCodigoCentroCusto("cc1");
        assertThat(noBanco).isPresent();
        assertThat(noBanco.get().getId()).isEqualTo(centroSalvo.getId());
        assertThat(noBanco.get().getNome()).isEqualTo("Montagem");
    }

    @Test
    @Transactional
    @Rollback
    void adicionaCentroDeCusto_NaoDeveAdicionarCentroDeCusto_QuandoCentroJaExiste() {
        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc0");
        centroCusto.setNome("Producao");

        repository.save(centroCusto);

        CentroCustoRequestDTO dto = new CentroCustoRequestDTO();
        dto.setCodigoCentroCusto(centroCusto.getCodigoCentroCusto());
        dto.setNome(centroCusto.getNome());

        assertThrows(IllegalArgumentException.class, () -> {
            service.adicionaCentroDeCusto(dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void atualizaCentroDeCusto_DeveAtualizarCentro_QuandoCentroJaExiste() {
        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Montagem");

        CentroCusto centroSalvo = repository.save(centroCusto);
        Long idCentroAntigo = centroSalvo.getId();

        CentroCustoRequestDTO dto = new CentroCustoRequestDTO();
        dto.setCodigoCentroCusto("Centro 01");
        dto.setNome("Setor Montagem");

        CentroCusto centroAtualizado = service.atualizaCentroDeCusto(idCentroAntigo, dto);

        assertThat(centroAtualizado).isNotNull();
        assertThat(centroAtualizado.getId()).isEqualTo(idCentroAntigo);

    }

    @Test
    @Transactional
    @Rollback
    void atualizaCentroDeCusto_NaoDeveAtualizarCentro_QuandoCentroNaoExiste() {
        Long idInexistente = 50L;

        CentroCustoRequestDTO dto = new CentroCustoRequestDTO();
        dto.setCodigoCentroCusto("Centro 01");
        dto.setNome("Setor Montagem");

        assertThrows(IllegalArgumentException.class, () -> {
            service.atualizaCentroDeCusto(idInexistente, dto);
        });


    }

    @Test
    @Transactional
    @Rollback
    void atualizaCentroDeCusto_NaoDeveAtualizarCentro_QuandoCodigoDoCentroJaExiste() {
        CentroCusto centroCusto1 = new CentroCusto();
        centroCusto1.setCodigoCentroCusto("cc1");
        centroCusto1.setNome("Montagem");

        repository.save(centroCusto1);

        CentroCusto centroCusto2 = new CentroCusto();
        centroCusto2.setCodigoCentroCusto("Centro 01");
        centroCusto2.setNome("Setor Montagem");

        CentroCusto centro2Salvo = repository.save(centroCusto2);

        CentroCustoRequestDTO dto = new CentroCustoRequestDTO();
        dto.setCodigoCentroCusto("cc1");
        dto.setNome("Setor Montagem");

        assertThrows(IllegalArgumentException.class, () -> {
            service.atualizaCentroDeCusto(centro2Salvo.getId(), dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void deletaCentroDeCusto_DeveDeletarCentro_QuandoCentroJaExiste() {
        CentroCusto centroCusto1 = new CentroCusto();
        centroCusto1.setCodigoCentroCusto("cc1");
        centroCusto1.setNome("Montagem");

        CentroCusto centroSalvo = repository.save(centroCusto1);
        Long idCentro = centroSalvo.getId();

        service.deletaCentroDeCusto(idCentro);

        Optional<CentroCusto> centroNoBanco = repository.findByCodigoCentroCusto("cc1");
        assertThat(centroNoBanco).isNotPresent();


    }

    @Test
    @Transactional
    @Rollback
    void deletaCentroDeCusto_NaoDeveDeletarCentro_QuandoCentroNaoExiste() {
        Long idInexistente = 5L;

        assertThrows(IllegalArgumentException.class, () -> {
            service.deletaCentroDeCusto(idInexistente);
        });
    }

    @Test
    @Transactional
    @Rollback
    void buscaPorCodigo_DeveRetornarCentro_QuandoCodigoExiste() {
        CentroCusto centroCusto = new CentroCusto();
        centroCusto.setCodigoCentroCusto("cc1");
        centroCusto.setNome("Montagem");

        CentroCusto centroSalvo = repository.save(centroCusto);

        service.buscaPorCodigo(centroSalvo.getCodigoCentroCusto());

        assertThat(centroSalvo.getCodigoCentroCusto()).isNotNull();
        assertThat(centroSalvo.getCodigoCentroCusto()).isEqualTo("cc1");
    }

    @Test
    @Transactional
    @Rollback
    void buscaPorCodigo_DisparaExcecao_QuandoCodigoNaoExiste() {
        String codigoCentroInexistente = "cc1";

        assertThrows(IllegalArgumentException.class, () -> {
           service.buscaPorCodigo(codigoCentroInexistente);
        });
    }

    @Test
    @Transactional
    @Rollback
    void listaTodosOsCentrosDeCusto_DeveRetornarTodosOsCentros_QuandoExistentes() {
        CentroCusto centroCusto1 = new CentroCusto();
        centroCusto1.setCodigoCentroCusto("cc1");
        centroCusto1.setNome("Montagem");

        CentroCusto centroCusto2 = new CentroCusto();
        centroCusto2.setCodigoCentroCusto("cc2");
        centroCusto2.setNome("Embalagem");

        CentroCusto centroCusto3 = new CentroCusto();
        centroCusto3.setCodigoCentroCusto("cc3");
        centroCusto3.setNome("Expedicao");

        repository.save(centroCusto1);
        repository.save(centroCusto2);
        repository.save(centroCusto3);

        service.listaTodosOsCentrosDeCustos();

        Optional<CentroCusto> centro1NoBanco = repository.findByCodigoCentroCusto("cc1");
        assertThat(centro1NoBanco).isPresent();
        Optional<CentroCusto> centro2NoBanco = repository.findByCodigoCentroCusto("cc2");
        assertThat(centro2NoBanco).isPresent();
        Optional<CentroCusto> centro3NoBanco = repository.findByCodigoCentroCusto("cc3");
        assertThat(centro3NoBanco).isPresent();
    }

}
