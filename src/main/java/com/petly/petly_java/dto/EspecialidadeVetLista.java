package com.petly.petly_java.dto;

import com.petly.petly_java.model.NomeEspecialidade;
import org.springframework.hateoas.Link;

public record EspecialidadeVetLista(NomeEspecialidade nome, Link link) {
}
