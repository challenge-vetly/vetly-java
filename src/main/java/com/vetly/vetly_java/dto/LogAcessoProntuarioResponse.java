package com.vetly.vetly_java.dto;

import com.vetly.vetly_java.model.BaseAcesso;

import java.time.LocalDateTime;
import java.util.UUID;

public record LogAcessoProntuarioResponse(
        UUID id,
        LocalDateTime dataHoraAcesso,
        String contextoAcesso,
        BaseAcesso baseAcesso,
        String veterinarioNome
) {
}
