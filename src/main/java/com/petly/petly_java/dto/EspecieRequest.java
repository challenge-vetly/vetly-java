package com.petly.petly_java.dto;

import com.petly.petly_java.model.NomeEspecie;
import com.petly.petly_java.validation.ValueOfEnum;
import jakarta.validation.constraints.NotNull;

public record EspecieRequest(
        @NotNull(message = "nao pode ser vazio")
        @ValueOfEnum(enumClass = NomeEspecie.class)
        String nome
) {
}
