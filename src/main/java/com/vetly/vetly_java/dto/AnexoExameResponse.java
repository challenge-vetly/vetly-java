package com.vetly.vetly_java.dto;

import java.time.LocalDate;
import java.util.UUID;

public record AnexoExameResponse(
        UUID id,
        String urlArquivo,
        String mimeType,
        LocalDate dataUpload
) {
}
