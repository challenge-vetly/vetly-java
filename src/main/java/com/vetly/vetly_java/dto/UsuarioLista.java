package com.vetly.vetly_java.dto;

import org.springframework.hateoas.Link;

public record UsuarioLista(String email, Link link) {
}
