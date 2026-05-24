package com.petly.petly_java.mapper;

import com.petly.petly_java.controller.VeterinarioController;
import com.petly.petly_java.dto.VeterinarioLista;
import com.petly.petly_java.dto.VeterinarioResponse;
import com.petly.petly_java.model.Veterinario;
import com.petly.petly_java.model.VeterinarioEspecialidade;
import com.petly.petly_java.repository.VeterinarioRepository;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class VeterinarioMapper {

    public VeterinarioLista veterinarioToVeterinarioLista(Veterinario veterinario) {
        Link link = linkTo(methodOn(VeterinarioController.class)
                .readVeterinario(veterinario.getId()))
                .withRel("detalhes");

        return new VeterinarioLista(
                veterinario.getPessoa().getNome(),
                veterinario.getCrmv(),
                veterinario.getEspecialidades()
                        .stream()
                        .map(VeterinarioEspecialidade::getEspecialidade)
                        .collect(Collectors.toList()),
                link
        );
    }

    public VeterinarioResponse veterinarioToVeterinarioResponse(Veterinario veterinario) {
        return new VeterinarioResponse(veterinario.getPessoa().getNome(), veterinario.getCrmv(),
                veterinario.getEspecialidades()
                        .stream()
                        .map(e -> e.getEspecialidade().getNome().toString())
                        .collect(Collectors.toList()));
    }
}