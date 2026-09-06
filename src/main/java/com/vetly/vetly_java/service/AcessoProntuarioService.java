package com.vetly.vetly_java.service;

import com.vetly.vetly_java.dto.EvolucaoClinicaResponse;
import com.vetly.vetly_java.dto.LogAcessoProntuarioResponse;
import com.vetly.vetly_java.model.*;
import com.vetly.vetly_java.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementa a "Colmeia" (RN-064 a RN-067): concessão automática de acesso ao
 * histórico clínico do animal para o veterinário com vínculo clínico ativo
 * (uma consulta não cancelada com o animal), com escopo dependente do
 * consentimento de rede do tutor — e o log obrigatório de todo acesso.
 */
@Service
public class AcessoProntuarioService {

    private final AnimalRepository animalRepository;
    private final ConsultaRepository consultaRepository;
    private final EvolucaoClinicaRepository evolucaoClinicaRepository;
    private final LogAcessoProntuarioRepository logAcessoProntuarioRepository;
    private final TutorRepository tutorRepository;
    private final VeterinarioRepository veterinarioRepository;

    public AcessoProntuarioService(AnimalRepository animalRepository, ConsultaRepository consultaRepository,
                                   EvolucaoClinicaRepository evolucaoClinicaRepository,
                                   LogAcessoProntuarioRepository logAcessoProntuarioRepository,
                                   TutorRepository tutorRepository, VeterinarioRepository veterinarioRepository) {
        this.animalRepository = animalRepository;
        this.consultaRepository = consultaRepository;
        this.evolucaoClinicaRepository = evolucaoClinicaRepository;
        this.logAcessoProntuarioRepository = logAcessoProntuarioRepository;
        this.tutorRepository = tutorRepository;
        this.veterinarioRepository = veterinarioRepository;
    }

    public List<EvolucaoClinicaResponse> historicoParaTutor(UUID animalId) {
        Tutor tutor = requireTutor(currentUsuario());
        Animal animal = findAnimal(animalId);
        if (!animal.getTutor().getId().equals(tutor.getId())) {
            throw new AccessDeniedException("Animal não pertence ao tutor autenticado");
        }

        return evolucaoClinicaRepository.findByAnimalId(animalId).stream()
                .filter(e -> e.isAlertaSeguranca() || !e.isOcultoResponsavel())
                .map(this::toResponse)
                .toList();
    }

    public List<EvolucaoClinicaResponse> historicoParaVeterinario(UUID animalId, String contexto) {
        Usuario usuario = currentUsuario();
        requireRole(usuario, UserRole.VETERINARIO, "Somente veterinários acessam o histórico pela Colmeia");
        Veterinario veterinario = requireVeterinario(usuario);
        Animal animal = findAnimal(animalId);

        exigirVinculoClinico(animal, veterinario);

        boolean consentimentoRedeAtivo = animal.getTutor().isConsentimentoRede();
        BaseAcesso base = consentimentoRedeAtivo ? BaseAcesso.CONSENTIMENTO_REDE : BaseAcesso.ATENDIMENTO_DIRETO;

        List<EvolucaoClinica> entradas = consentimentoRedeAtivo
                ? evolucaoClinicaRepository.findByAnimalId(animalId)
                : evolucaoClinicaRepository.findByAnimalIdAndVeterinarioId(animalId, veterinario.getId());

        registrarAcesso(animal, veterinario, base, contexto);

        return entradas.stream().map(this::toResponse).toList();
    }

    /**
     * RN-064: gate de vínculo clínico da Colmeia — usado tanto para o histórico
     * (EvolucaoClinica) quanto para o Prontuário (ProntuarioService).
     */
    public void exigirVinculoClinico(Animal animal, Veterinario veterinario) {
        boolean temVinculoClinico = consultaRepository
                .existsByAnimalAndVeterinarioAndStatusNot(animal, veterinario, StatusConsulta.CANCELADA);
        if (!temVinculoClinico) {
            throw new AccessDeniedException(
                    "Colmeia: veterinário não possui nenhuma consulta com este animal — acesso não concedido");
        }
    }

    /**
     * RN-067: todo acesso de um veterinário ao prontuário/histórico do animal
     * pela Colmeia gera log, visível ao Responsável.
     */
    public void registrarAcesso(Animal animal, Veterinario veterinario, BaseAcesso base, String contexto) {
        LogAcessoProntuario log = new LogAcessoProntuario(
                UUID.randomUUID(), LocalDateTime.now(), contexto, base, animal, veterinario);
        logAcessoProntuarioRepository.save(log);
    }

    public Page<LogAcessoProntuarioResponse> logsDeAcesso(UUID animalId, Pageable pageable) {
        Tutor tutor = requireTutor(currentUsuario());
        Animal animal = findAnimal(animalId);
        if (!animal.getTutor().getId().equals(tutor.getId())) {
            throw new AccessDeniedException("Animal não pertence ao tutor autenticado");
        }

        return logAcessoProntuarioRepository.findByAnimal(animal, pageable).map(this::toLogResponse);
    }

    private EvolucaoClinicaResponse toResponse(EvolucaoClinica evolucao) {
        return new EvolucaoClinicaResponse(
                evolucao.getId(),
                evolucao.getAnotacoes(),
                evolucao.getConsulta().getDataHora(),
                evolucao.getConsulta().getVeterinario().getPessoa().getNome(),
                evolucao.isAlertaSeguranca()
        );
    }

    private LogAcessoProntuarioResponse toLogResponse(LogAcessoProntuario log) {
        return new LogAcessoProntuarioResponse(
                log.getId(),
                log.getDataHoraAcesso(),
                log.getContextoAcesso(),
                log.getBaseAcesso(),
                log.getVeterinario().getPessoa().getNome()
        );
    }

    private Animal findAnimal(UUID animalId) {
        return animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Animal não encontrado: " + animalId));
    }

    private void requireRole(Usuario usuario, UserRole role, String message) {
        if (usuario.getRole() != role) {
            throw new AccessDeniedException(message);
        }
    }

    private Tutor requireTutor(Usuario usuario) {
        requireRole(usuario, UserRole.TUTOR, "Somente tutores podem realizar esta ação");
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
