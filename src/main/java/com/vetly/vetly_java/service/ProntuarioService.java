package com.vetly.vetly_java.service;

import com.vetly.vetly_java.dto.ProntuarioCorrecaoRequest;
import com.vetly.vetly_java.dto.ProntuarioRequest;
import com.vetly.vetly_java.dto.ProntuarioResponse;
import com.vetly.vetly_java.mapper.ProntuarioMapper;
import com.vetly.vetly_java.model.*;
import com.vetly.vetly_java.repository.AnimalRepository;
import com.vetly.vetly_java.repository.ProntuarioRepository;
import com.vetly.vetly_java.repository.TutorRepository;
import com.vetly.vetly_java.repository.VeterinarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * RN-063, RN-088/089: prontuário único por animal (não do vet), corrigível
 * sem sobrescrever a versão original — cada correção é uma nova linha
 * apontando para a original, com justificativa obrigatória fora da janela
 * de 24h.
 */
@Service
public class ProntuarioService {

    private final ProntuarioRepository prontuarioRepository;
    private final AnimalRepository animalRepository;
    private final TutorRepository tutorRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final AcessoProntuarioService acessoProntuarioService;
    private final ProntuarioMapper mapper;

    public ProntuarioService(ProntuarioRepository prontuarioRepository, AnimalRepository animalRepository,
                             TutorRepository tutorRepository, VeterinarioRepository veterinarioRepository,
                             AcessoProntuarioService acessoProntuarioService, ProntuarioMapper mapper) {
        this.prontuarioRepository = prontuarioRepository;
        this.animalRepository = animalRepository;
        this.tutorRepository = tutorRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.acessoProntuarioService = acessoProntuarioService;
        this.mapper = mapper;
    }

    public ProntuarioResponse criar(UUID animalId, ProntuarioRequest request) {
        Animal animal = findAnimal(animalId);
        Veterinario veterinario = requireVeterinarioComVinculo(animal);

        if (prontuarioRepository.findByAnimalAndOriginalIsNull(animal).isPresent()) {
            throw new IllegalArgumentException("Este animal já possui um prontuário — use a correção para atualizá-lo");
        }

        Prontuario prontuario = new Prontuario(UUID.randomUUID(), LocalDate.now(), animal, request.conteudoClinico());
        prontuario = prontuarioRepository.save(prontuario);

        acessoProntuarioService.registrarAcesso(animal, veterinario, BaseAcesso.ATENDIMENTO_DIRETO,
                "Criação do prontuário");

        return mapper.toResponse(prontuario);
    }

    public List<ProntuarioResponse> ler(UUID animalId) {
        Animal animal = findAnimal(animalId);
        Usuario usuario = currentUsuario();

        if (usuario.getRole() == UserRole.TUTOR) {
            Tutor tutor = requireTutor(usuario);
            if (!animal.getTutor().getId().equals(tutor.getId())) {
                throw new AccessDeniedException("Animal não pertence ao tutor autenticado");
            }
        } else if (usuario.getRole() == UserRole.VETERINARIO) {
            Veterinario veterinario = requireVeterinario(usuario);
            acessoProntuarioService.exigirVinculoClinico(animal, veterinario);
            acessoProntuarioService.registrarAcesso(animal, veterinario, BaseAcesso.ATENDIMENTO_DIRETO,
                    "Leitura do prontuário");
        } else {
            throw new AccessDeniedException("Somente tutores ou veterinários podem acessar o prontuário");
        }

        Prontuario original = prontuarioRepository.findByAnimalAndOriginalIsNull(animal)
                .orElseThrow(() -> new EntityNotFoundException("Animal ainda não possui prontuário: " + animalId));

        List<Prontuario> historico = new ArrayList<>();
        historico.add(original);
        historico.addAll(prontuarioRepository.findByOriginalOrderByDataHoraCorrecaoAsc(original));

        return historico.stream().map(mapper::toResponse).toList();
    }

    public ProntuarioResponse corrigir(UUID animalId, ProntuarioCorrecaoRequest request) {
        Animal animal = findAnimal(animalId);
        Veterinario veterinario = requireVeterinarioComVinculo(animal);

        Prontuario original = prontuarioRepository.findByAnimalAndOriginalIsNull(animal)
                .orElseThrow(() -> new EntityNotFoundException("Animal ainda não possui prontuário: " + animalId));

        boolean dentroDaJanelaDe24h = !LocalDate.now().isAfter(original.getDataUltimaAtualizacao());
        if (!dentroDaJanelaDe24h && (request.justificativa() == null || request.justificativa().isBlank())) {
            throw new IllegalArgumentException(
                    "Correção fora da janela de 24h da criação do prontuário original exige justificativa (RN-089)");
        }

        Prontuario correcao = new Prontuario(UUID.randomUUID(), LocalDate.now(), animal, request.conteudoClinico());
        correcao.setOriginal(original);
        correcao.setDataHoraCorrecao(LocalDateTime.now());
        correcao.setCrmvSolicitanteCorrecao(veterinario.getCrmv());
        correcao.setJustificativaCorrecao(dentroDaJanelaDe24h ? null : request.justificativa());
        correcao = prontuarioRepository.save(correcao);

        acessoProntuarioService.registrarAcesso(animal, veterinario, BaseAcesso.ATENDIMENTO_DIRETO,
                "Correção do prontuário");

        return mapper.toResponse(correcao);
    }

    private Veterinario requireVeterinarioComVinculo(Animal animal) {
        Usuario usuario = currentUsuario();
        if (usuario.getRole() != UserRole.VETERINARIO) {
            throw new AccessDeniedException("Somente veterinários podem escrever no prontuário");
        }
        Veterinario veterinario = requireVeterinario(usuario);
        acessoProntuarioService.exigirVinculoClinico(animal, veterinario);
        return veterinario;
    }

    private Animal findAnimal(UUID animalId) {
        return animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Animal não encontrado: " + animalId));
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
