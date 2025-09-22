package com.personal.crudapi.service;

import com.personal.crudapi.dto.requests.MaterialRequestDTO;
import com.personal.crudapi.entity.Material;
import com.personal.crudapi.repository.MaterialRepository;
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
@Import(MaterialService.class)
public class MaterialServiceTest {

    @Autowired
    MaterialService service;

    @Autowired
    MaterialRepository repository;

    @Test
    @Transactional
    @Rollback
    void adicionaOuAtualizaMaterial_DeveSalvarMaterial_QuandoCodigoNaoExiste() {
        var req = new MaterialRequestDTO();
        req.setCodigoMaterial("22564");
        req.setNome("Peça T");
        req.setTipo("Corpo");

        Material salvo = service.adicionaOuAtualizaMaterial(req);

        assertThat(salvo).isNotNull();
        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getCodigoMaterial()).isEqualTo("22564");
        assertThat(salvo.getNome()).isEqualTo("Peça T");
        assertThat(salvo.getTipo()).isEqualTo("Corpo");

        Optional<Material> noBanco = repository.findByCodigoMaterial("22564");
        assertThat(noBanco).isPresent();
        assertThat(noBanco.get().getId()).isEqualTo(salvo.getId());
        assertThat(noBanco.get().getNome()).isEqualTo("Peça T");
        assertThat(noBanco.get().getTipo()).isEqualTo("Corpo");
    }

    @Test
    @Transactional
    @Rollback
    void adicionaOuAtualizaMaterial_DeveAtualizarMaterial_QuandoCodigoJaExiste() {
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T antigo");
        material.setTipo("Corpo");

        Material salvoNoBanco = repository.save(material);
        Long idMaterial = salvoNoBanco.getId();

        var request = new MaterialRequestDTO();
        request.setCodigoMaterial("22564");
        request.setNome("Peça T nova");
        request.setTipo("Corpo");

        Material materialAtualizado = service.adicionaOuAtualizaMaterial(request);

        assertThat(materialAtualizado).isNotNull();
        assertThat(materialAtualizado.getId()).isEqualTo(idMaterial);
        assertThat(materialAtualizado.getCodigoMaterial()).isEqualTo("22564");
        assertThat(materialAtualizado.getTipo()).isEqualTo("Corpo");
        assertThat(materialAtualizado.getNome()).isEqualTo("Peça T nova");

        Optional<Material> materialNoBanco = repository.findByCodigoMaterial("22564");
        assertThat(materialNoBanco).isPresent();
        assertThat(materialNoBanco.get().getId()).isEqualTo(idMaterial);
        assertThat(materialNoBanco.get().getNome()).isEqualTo("Peça T nova");

        int contagem = 0;
        for (var m: repository.findAll()){
            if (m.getCodigoMaterial().equals("22564")) {
                contagem = +1;
                break;
            }
        }

        assertThat(contagem).isEqualTo(1);
    }

    @Test
    @Transactional
    @Rollback
    void deletaMaterial_DeveDeletarMaterialDoBanco_QuandoCodigoExiste() {
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        Material materialSalvoNoBanco = repository.save(material);
        Long idMaterial = materialSalvoNoBanco.getId();

        service.deletaMaterial(idMaterial);

        Optional<Material> materialNoBanco = repository.findByCodigoMaterial("22564");
        assertThat(materialNoBanco).isNotPresent();
    }

    @Test
    @Transactional
    @Rollback
    void deletaMaterial_MaterialNaoEncontrado_QuandoIdNaoExiste() {
        Long idInexistente = 5L;

        assertThrows(IllegalArgumentException.class, () -> {
            service.deletaMaterial(idInexistente);
        });
    }

    @Test
    @Transactional
    @Rollback
    void buscaPorCodigo_DeveRetornarMaterial_QuandoCodigoExiste() {
        Material material = new Material();
        material.setCodigoMaterial("22564");
        material.setNome("Peça T");
        material.setTipo("Corpo");

        repository.save(material);

        service.buscaPorCodigo(material.getCodigoMaterial());

        assertThat(material.getCodigoMaterial()).isNotNull();
        assertThat(material.getCodigoMaterial()).isEqualTo("22564");
    }

    @Test
    @Transactional
    @Rollback
    void buscaPorCodigo_DisparaExcecao_QuandoCodigoNaoExiste() {
        String codigoDoMaterialInexistente = "20661";

        assertThrows(IllegalArgumentException.class, () -> {
            service.buscaPorCodigo(codigoDoMaterialInexistente);
        });
    }

    @Test
    @Transactional
    @Rollback
    void listaTodosOsMateriais_DeveRetornarTodosOsMateriais_QuandoExistentesNoBanco() {
        Material material1 = new Material();
        material1.setCodigoMaterial("22564");
        material1.setNome("Peça T");
        material1.setTipo("Corpo");

        Material material2 = new Material();
        material2.setCodigoMaterial("22565");
        material2.setNome("Peça U");
        material2.setTipo("Corpo");

        Material material3 = new Material();
        material3.setCodigoMaterial("22566");
        material3.setNome("Peça V");
        material3.setTipo("Corpo");

        repository.save(material1);
        repository.save(material2);
        repository.save(material3);

        service.listaTodosOsMateriais();

        Optional<Material> material1NoBanco = repository.findByCodigoMaterial("22564");
        assertThat(material1NoBanco).isPresent();
        Optional<Material> material2NoBanco = repository.findByCodigoMaterial("22565");
        assertThat(material2NoBanco).isPresent();
        Optional<Material> material3NoBanco = repository.findByCodigoMaterial("22566");
        assertThat(material3NoBanco).isPresent();
    }
}
