package com.vetly.vetly_java.dto;

import java.time.LocalDate;

public record TutorConsentimentoResponse(
        boolean lgpdAceito,
        LocalDate dataLgpdAceito,
        boolean consentimentoRede,
        LocalDate dataConsentimentoRede
) {
}
