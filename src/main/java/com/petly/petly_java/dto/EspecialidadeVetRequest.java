package com.petly.petly_java.dto;

import com.petly.petly_java.model.NomeEspecialidade;
import jakarta.validation.constraints.*;

public record EspecialidadeVetRequest(
        @NotNull
        NomeEspecialidade nome,
        @NotBlank(message = "O nome e obrigatorio")
        @Size(max = 150, message = "A descricao deve ter no maximo 150 caracteres")
        String descricao
) {
}