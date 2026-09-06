package com.vetly.vetly_java.mapper;

import com.vetly.vetly_java.dto.*;
import com.vetly.vetly_java.model.AnexoExame;
import com.vetly.vetly_java.model.Consulta;
import com.vetly.vetly_java.model.SolicitacaoExame;
import com.vetly.vetly_java.model.SolicitacaoExameItem;
import com.vetly.vetly_java.model.StatusExame;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class SolicitacaoExameMapper {

    public SolicitacaoExame solicitacaoExameRequestToSolicitacaoExame(SolicitacaoExameRequest request, Consulta consulta) {
        SolicitacaoExame solicitacao = new SolicitacaoExame();
        solicitacao.setObservacao(request.observacao());
        solicitacao.setConsulta(consulta);

        List<SolicitacaoExameItem> itens = new ArrayList<>();
        for (SolicitacaoExameRequest.ItemRequest itemRequest : request.itens()) {
            SolicitacaoExameItem item = new SolicitacaoExameItem();
            item.setNomeExame(itemRequest.nomeExame());
            item.setStatus(StatusExame.SOLICITADO);
            item.setDataSolicitacao(LocalDate.now());
            item.setLiberadoResponsavel("N");
            item.setSolicitacaoExame(solicitacao);
            itens.add(item);
        }
        solicitacao.setItens(itens);
        return solicitacao;
    }

    public SolicitacaoExameResponse toResponse(SolicitacaoExame solicitacao, boolean restringirParaTutor) {
        List<SolicitacaoExameItemResponse> itens = solicitacao.getItens().stream()
                .map(item -> toItemResponse(item, restringirParaTutor))
                .toList();

        return new SolicitacaoExameResponse(
                solicitacao.getId(),
                solicitacao.getObservacao(),
                solicitacao.getConsulta().getId(),
                solicitacao.getConsulta().getAnimal().getNome(),
                solicitacao.getConsulta().getVeterinario().getPessoa().getNome(),
                itens
        );
    }

    public SolicitacaoExameItemResponse toItemResponse(SolicitacaoExameItem item, boolean restringirParaTutor) {
        boolean ocultarResultado = restringirParaTutor && !item.isLiberadoResponsavel();

        List<AnexoExameResponse> anexos = ocultarResultado
                ? List.of()
                : item.getAnexos().stream().map(this::toAnexoResponse).toList();

        return new SolicitacaoExameItemResponse(
                item.getId(),
                item.getNomeExame(),
                item.getStatus(),
                item.getDataSolicitacao(),
                ocultarResultado ? null : item.getDataResultado(),
                ocultarResultado ? null : item.getDescricaoResultado(),
                item.isLiberadoResponsavel(),
                item.getDataLiberacaoResponsavel(),
                anexos
        );
    }

    private AnexoExameResponse toAnexoResponse(AnexoExame anexo) {
        return new AnexoExameResponse(anexo.getId(), anexo.getUrlArquivo(), anexo.getMimeType(), anexo.getDataUpload());
    }
}
