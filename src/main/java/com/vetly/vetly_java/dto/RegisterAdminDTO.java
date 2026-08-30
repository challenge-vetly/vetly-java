package com.vetly.vetly_java.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterAdminDTO(
        @NotBlank(message = "nao pode ser vazio")
        @Email(message = "Email inválido")
        String email,
        @NotBlank(message = "nao pode ser vazio")
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
        String senha) {
}
