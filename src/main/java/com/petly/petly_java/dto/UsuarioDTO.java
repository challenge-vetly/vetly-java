package com.petly.petly_java.dto;

import com.petly.petly_java.model.UserRole;
import jakarta.validation.constraints.*;

public record UsuarioDTO(
        @NotBlank
        @Email
        String email,

        @NotNull
        UserRole role,

        @NotNull
        @Pattern(regexp = "[SN]", message = "flagAtivo deve ser 'S' ou 'N'")
        String flagAtivo,

        @NotBlank
        @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = """
            A senha deve conter:
            - mínimo 8 caracteres
            - uma letra maiúscula
            - uma letra minúscula
            - um número
            - um caractere especial
            """
        )
        String senha
) {
}
