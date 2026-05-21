package com.petly.petly_java.dto;

import com.petly.petly_java.model.NomeEspecie;
import jakarta.validation.constraints.NotNull;

public record EspecieRequest(
        @NotNull(message = "O nome nao pode ser nulo")
        NomeEspecie nome
) {
}
