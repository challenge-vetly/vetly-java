package com.vetly.vetly_java.dto;

import com.vetly.vetly_java.model.UserRole;

public record UsuarioResponse(
        String id,
        String email,
        UserRole role,
        String flagAtivo
) {
}
