package com.petly.petly_java.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AuthDTO(
        @NotNull(message = "O email é obrigatório")
        @Email(message = "Email inválido")
        String login,
        @NotBlank(message = "A senha é obrigatória")
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
