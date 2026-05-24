package com.petly.petly_java.dto;

import org.springframework.hateoas.Link;

import java.util.List;

public record VeterinarioResponse(
        String nome,
        String crmv,
        List<String> especialidades
) {
}
