package com.vetly.vetly_java.mapper;

import com.vetly.vetly_java.dto.ProntuarioResponse;
import com.vetly.vetly_java.model.Prontuario;
import org.springframework.stereotype.Component;

@Component
public class ProntuarioMapper {

    public ProntuarioResponse toResponse(Prontuario prontuario) {
        return new ProntuarioResponse(
                prontuario.getId(),
                prontuario.getConteudoClinico(),
                prontuario.getDataUltimaAtualizacao(),
                prontuario.isOriginal(),
                prontuario.getDataHoraCorrecao(),
                prontuario.getCrmvSolicitanteCorrecao(),
                prontuario.getJustificativaCorrecao()
        );
    }
}
