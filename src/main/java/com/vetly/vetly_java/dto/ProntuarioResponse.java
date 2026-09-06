package com.vetly.vetly_java.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProntuarioResponse(
        UUID id,
        String conteudoClinico,
        LocalDate dataUltimaAtualizacao,
        boolean original,
        LocalDateTime dataHoraCorrecao,
        String crmvSolicitanteCorrecao,
        String justificativaCorrecao
) {
}
