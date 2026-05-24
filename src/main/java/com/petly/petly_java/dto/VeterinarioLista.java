package com.petly.petly_java.dto;

import com.petly.petly_java.model.EspecialidadeVet;
import org.springframework.hateoas.Link;

import java.util.List;

public record VeterinarioLista(
        String nome,
        String crmv,
        List<EspecialidadeVet> especialidadeVet,
        Link link
) {
}
