package com.vetly.vetly_java.dto;

import com.vetly.vetly_java.model.StatusExame;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SolicitacaoExameItemResponse(
        UUID id,
        String nomeExame,
        StatusExame status,
        LocalDate dataSolicitacao,
        LocalDate dataResultado,
        String descricaoResultado,
        boolean liberadoResponsavel,
        LocalDate dataLiberacaoResponsavel,
        List<AnexoExameResponse> anexos
) {
}
