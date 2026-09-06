package com.vetly.vetly_java.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProntuarioCorrecaoRequest(
        @NotBlank(message = "nao pode ser vazio")
        String conteudoClinico,

        @Size(max = 1000, message = "maximo de 1000 caracteres")
        String justificativa
) {
}
