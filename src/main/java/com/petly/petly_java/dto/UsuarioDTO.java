package com.petly.petly_java.dto;

import com.petly.petly_java.model.UserRole;
import com.petly.petly_java.validation.ValueOfEnum;
import jakarta.validation.constraints.*;

public record UsuarioDTO(
        @NotBlank(message = "nao pode ser vazio")
        @Email(message = "email invalido")
        String email,

        @NotNull(message = "nao pode ser vazio")
        @ValueOfEnum(enumClass = UserRole.class)
        String role,

        @NotNull(message = "nao pode ser vazio")
        @Pattern(regexp = "[SN]", message = "flagAtivo deve ser 'S' ou 'N'")
        String flagAtivo,

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
        String senha
) {
}
