package com.vetly.vetly_java.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record EvolucaoClinicaResponse(
        UUID id,
        String anotacoes,
        LocalDateTime dataConsulta,
        String veterinarioNome,
        boolean alertaSeguranca
) {
}
