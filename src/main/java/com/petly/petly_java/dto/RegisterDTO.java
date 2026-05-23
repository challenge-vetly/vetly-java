package com.petly.petly_java.dto;

import com.petly.petly_java.model.UserRole;
import com.petly.petly_java.validation.ValueOfEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RegisterDTO(
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
        String senha,
        @NotNull(message = "nao pode ser vazio")
        @ValueOfEnum(enumClass = UserRole.class)
        String role) {
}
