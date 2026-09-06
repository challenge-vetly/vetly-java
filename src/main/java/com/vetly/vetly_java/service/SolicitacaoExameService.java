package com.vetly.vetly_java.service;

import com.vetly.vetly_java.dto.*;
import com.vetly.vetly_java.mapper.SolicitacaoExameMapper;
import com.vetly.vetly_java.model.*;
import com.vetly.vetly_java.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class SolicitacaoExameService {

    private final SolicitacaoExameRepository solicitacaoExameRepository;
    private final SolicitacaoExameItemRepository solicitacaoExameItemRepository;
    private final ConsultaRepository consultaRepository;
    private final TutorRepository tutorRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final SolicitacaoExameMapper mapper;

    public SolicitacaoExameService(SolicitacaoExameRepository solicitacaoExameRepository,
                                   SolicitacaoExameItemRepository solicitacaoExameItemRepository,
                                   ConsultaRepository consultaRepository, TutorRepository tutorRepository,
                                   VeterinarioRepository veterinarioRepository, SolicitacaoExameMapper mapper) {
        this.solicitacaoExameRepository = solicitacaoExameRepository;
        this.solicitacaoExameItemRepository = solicitacaoExameItemRepository;
        this.consultaRepository = consultaRepository;
        this.tutorRepository = tutorRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.mapper = mapper;
    }

    public SolicitacaoExameResponse create(UUID consultaId, SolicitacaoExameRequest request) {
        Consulta consulta = findConsultaDoVeterinario(consultaId);

        if (solicitacaoExameRepository.findByConsulta(consulta).isPresent()) {
            throw new IllegalArgumentException("Já existe uma solicitação de exame para esta consulta");
        }

        SolicitacaoExame solicitacao = mapper.solicitacaoExameRequestToSolicitacaoExame(request, consulta);
        return mapper.toResponse(solicitacaoExameRepository.save(solicitacao), false);
    }

    public SolicitacaoExameResponse readPorConsulta(UUID consultaId) {
        Consulta consulta = findConsultaVisivel(consultaId);
        SolicitacaoExame solicitacao = solicitacaoExameRepository.findByConsulta(consulta)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não possui solicitação de exame: " + consultaId));

        boolean restringirParaTutor = currentUsuario().getRole() == UserRole.TUTOR;
        return mapper.toResponse(solicitacao, restringirParaTutor);
    }

    public SolicitacaoExameItemResponse registrarResultado(UUID itemId, ResultadoExameRequest request) {
        SolicitacaoExameItem item = findItemDoVeterinario(itemId);
        if (item.getStatus() != StatusExame.SOLICITADO && item.getStatus() != StatusExame.AGUARDANDO_RESULTADO) {
            throw new IllegalArgumentException(
                    "Só é possível registrar resultado a partir de SOLICITADO ou AGUARDANDO_RESULTADO — status atual: " + item.getStatus());
        }

        item.setDescricaoResultado(request.descricaoResultado());
        item.setDataResultado(LocalDate.now());
        item.setDataAnalise(LocalDate.now());
        item.setStatus(StatusExame.ANALISADO);

        return mapper.toItemResponse(solicitacaoExameItemRepository.save(item), false);
    }

    public AnexoExameResponse anexarArquivo(UUID itemId, AnexoExameRequest request) {
        SolicitacaoExameItem item = findItemDoVeterinario(itemId);

        AnexoExame anexo = new AnexoExame(UUID.randomUUID(), request.urlArquivo(), request.mimeType(), LocalDate.now(), item);
        item.getAnexos().add(anexo);
        solicitacaoExameItemRepository.save(item);

        return new AnexoExameResponse(anexo.getId(), anexo.getUrlArquivo(), anexo.getMimeType(), anexo.getDataUpload());
    }

    public SolicitacaoExameItemResponse liberarParaResponsavel(UUID itemId) {
        SolicitacaoExameItem item = findItemDoVeterinario(itemId);
        if (item.getStatus() != StatusExame.ANALISADO) {
            throw new IllegalArgumentException(
                    "Só é possível liberar ao Responsável um exame já ANALISADO — status atual: " + item.getStatus());
        }

        item.setLiberadoResponsavel("S");
        item.setDataLiberacaoResponsavel(LocalDate.now());
        item.setStatus(StatusExame.RESULTADO_ENVIADO);

        return mapper.toItemResponse(solicitacaoExameItemRepository.save(item), false);
    }

    public SolicitacaoExameItemResponse cancelar(UUID itemId) {
        SolicitacaoExameItem item = findItemDoVeterinario(itemId);
        if (item.getStatus() == StatusExame.RESULTADO_ENVIADO || item.getStatus() == StatusExame.CANCELADO) {
            throw new IllegalArgumentException(
                    "Não é possível cancelar um exame já enviado ou já cancelado — status atual: " + item.getStatus());
        }

        item.setStatus(StatusExame.CANCELADO);
        return mapper.toItemResponse(solicitacaoExameItemRepository.save(item), false);
    }

    private SolicitacaoExameItem findItemDoVeterinario(UUID itemId) {
        SolicitacaoExameItem item = solicitacaoExameItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item de solicitação de exame não encontrado: " + itemId));

        Usuario usuario = currentUsuario();
        requireRole(usuario, UserRole.VETERINARIO, "Somente o veterinário responsável pode gerenciar exames");
        Veterinario veterinario = requireVeterinario(usuario);

        if (!item.getSolicitacaoExame().getConsulta().getVeterinario().getId().equals(veterinario.getId())) {
            throw new AccessDeniedException("Exame não pertence a uma consulta do veterinário autenticado");
        }
        return item;
    }

    private Consulta findConsultaDoVeterinario(UUID consultaId) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada: " + consultaId));

        Usuario usuario = currentUsuario();
        requireRole(usuario, UserRole.VETERINARIO, "Somente o veterinário da consulta pode solicitar exames");
        Veterinario veterinario = requireVeterinario(usuario);

        if (!consulta.getVeterinario().getId().equals(veterinario.getId())) {
            throw new AccessDeniedException("Consulta não pertence ao veterinário autenticado");
        }
        return consulta;
    }

    private Consulta findConsultaVisivel(UUID consultaId) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada: " + consultaId));

        Usuario usuario = currentUsuario();
        if (usuario.getRole() == UserRole.TUTOR) {
            Tutor tutor = requireTutor(usuario);
            if (!consulta.getAnimal().getTutor().getId().equals(tutor.getId())) {
                throw new AccessDeniedException("Consulta não pertence ao tutor autenticado");
            }
        } else if (usuario.getRole() == UserRole.VETERINARIO) {
            Veterinario veterinario = requireVeterinario(usuario);
            if (!consulta.getVeterinario().getId().equals(veterinario.getId())) {
                throw new AccessDeniedException("Consulta não pertence ao veterinário autenticado");
            }
        } else {
            throw new AccessDeniedException("Somente tutores ou veterinários podem acessar solicitações de exame");
        }
        return consulta;
    }

    private void requireRole(Usuario usuario, UserRole role, String message) {
        if (usuario.getRole() != role) {
            throw new AccessDeniedException(message);
        }
    }

    private Tutor requireTutor(Usuario usuario) {
        Tutor tutor = tutorRepository.findByUsuario(usuario);
        if (tutor == null) {
            throw new EntityNotFoundException("Tutor não encontrado para o usuário autenticado");
        }
        return tutor;
    }

    private Veterinario requireVeterinario(Usuario usuario) {
        Veterinario veterinario = veterinarioRepository.findByUsuario(usuario);
        if (veterinario == null) {
            throw new EntityNotFoundException("Veterinário não encontrado para o usuário autenticado");
        }
        return veterinario;
    }

    private Usuario currentUsuario() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
