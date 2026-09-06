package com.vetly.vetly_java.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnexoExameRequest(
        @NotBlank(message = "nao pode ser vazio")
        @Size(max = 500, message = "maximo de 500 caracteres")
        String urlArquivo,

        @NotBlank(message = "nao pode ser vazio")
        @Size(max = 30, message = "maximo de 30 caracteres")
        String mimeType
) {
}
