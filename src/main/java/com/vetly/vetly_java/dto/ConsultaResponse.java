package com.vetly.vetly_java.dto;

import com.vetly.vetly_java.model.StatusConsulta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ConsultaResponse(
        UUID id,
        LocalDateTime dataHora,
        StatusConsulta status,
        BigDecimal valor,
        String observacao,
        UUID animalId,
        String animalNome,
        String veterinarioId,
        String veterinarioNome
) {
}
