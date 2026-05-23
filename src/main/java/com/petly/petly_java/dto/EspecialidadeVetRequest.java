package com.petly.petly_java.dto;

import com.petly.petly_java.model.NomeEspecialidade;
import com.petly.petly_java.validation.ValueOfEnum;
import jakarta.validation.constraints.*;

public record EspecialidadeVetRequest(
        @NotNull(message = "nao pode ser vazio")
        @ValueOfEnum(enumClass = NomeEspecialidade.class)
        String nome,
        @NotBlank(message = "nao pode ser vazio")
        @Size(max = 150, message = "A descricao deve ter no maximo 150 caracteres")
        String descricao
) {
}