package com.vetly.vetly_java.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResultadoExameRequest(
        @NotBlank(message = "nao pode ser vazio")
        @Size(max = 500, message = "maximo de 500 caracteres")
        String descricaoResultado
) {
}
