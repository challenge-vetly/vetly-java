package com.vetly.vetly_java.dto;

import jakarta.validation.constraints.NotBlank;

public record ProntuarioRequest(
        @NotBlank(message = "nao pode ser vazio")
        String conteudoClinico
) {
}
