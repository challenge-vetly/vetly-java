package com.vetly.vetly_java.dto;

import com.vetly.vetly_java.model.NomeEspecialidade;
import org.springframework.hateoas.Link;

public record EspecialidadeVetLista(NomeEspecialidade nome, Link link) {
}
