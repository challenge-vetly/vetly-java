package com.vetly.vetly_java.dto;

import java.util.List;
import java.util.UUID;

public record SolicitacaoExameResponse(
        UUID id,
        String observacao,
        UUID consultaId,
        String animalNome,
        String veterinarioNome,
        List<SolicitacaoExameItemResponse> itens
) {
}
