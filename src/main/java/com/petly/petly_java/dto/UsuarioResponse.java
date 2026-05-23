package com.petly.petly_java.dto;

import com.petly.petly_java.model.UserRole;

public record UsuarioResponse(
        String id,
        String email,
        UserRole role,
        String flagAtivo
) {
}
