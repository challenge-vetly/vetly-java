package com.vetly.vetly_java.dto;

import jakarta.validation.constraints.NotNull;

public record TutorConsentimentoRequest(
        @NotNull(message = "nao pode ser vazio")
        Boolean lgpdAceito,

        @NotNull(message = "nao pode ser vazio")
        Boolean consentimentoRede
) {
}
