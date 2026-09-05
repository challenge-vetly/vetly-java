package com.vetly.vetly_java.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ConsultaReagendarRequest(
        @NotNull(message = "nao pode ser vazio")
        @Future(message = "deve ser uma data futura")
        LocalDateTime dataHora
) {
}
