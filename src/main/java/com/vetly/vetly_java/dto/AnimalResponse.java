package com.vetly.vetly_java.dto;

import com.vetly.vetly_java.model.Sexo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AnimalResponse(
        UUID id,
        String nome,
        String raca,
        Sexo sexo,
        LocalDate dataNascimento,
        BigDecimal peso,
        String especie,
        String urlFoto,
        boolean castrado,
        String condicoesPreexistentes,
        String alergias,
        String medicacoesEmUso
) {
}
