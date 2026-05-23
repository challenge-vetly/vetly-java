package com.petly.petly_java.mapper;

import com.petly.petly_java.controller.EspecialidadeVetController;
import com.petly.petly_java.dto.EspecialidadeVetLista;
import com.petly.petly_java.dto.EspecialidadeVetRequest;
import com.petly.petly_java.model.EspecialidadeVet;
import com.petly.petly_java.model.NomeEspecialidade;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EspecialidadeVetMapper {
    public EspecialidadeVet especialidadeVetRequestToEspecialidadeVet(EspecialidadeVetRequest req) {
        EspecialidadeVet entity = new EspecialidadeVet();
        entity.setId(UUID.randomUUID().toString());
        entity.setNome(NomeEspecialidade.valueOf(req.nome()));
        entity.setDescricao(req.descricao());
        return entity;
    }
    public EspecialidadeVetLista especialidadeVetToEspecialidadeVetLista(EspecialidadeVet especialidadeVet) {
        Link link = linkTo(methodOn(EspecialidadeVetController.class).readEspecialidadeVetById(especialidadeVet.getId())).withRel("Descricao da especialidade");

        return new EspecialidadeVetLista(especialidadeVet.getNome(), link);
    }
}
