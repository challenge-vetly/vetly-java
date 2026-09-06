package com.vetly.vetly_java.service;

import com.vetly.vetly_java.dto.ConsultaReagendarRequest;
import com.vetly.vetly_java.dto.ConsultaRequest;
import com.vetly.vetly_java.dto.ConsultaResponse;
import com.vetly.vetly_java.mapper.ConsultaMapper;
import com.vetly.vetly_java.model.*;
import com.vetly.vetly_java.repository.AnimalRepository;
import com.vetly.vetly_java.repository.ConsultaRepository;
import com.vetly.vetly_java.repository.TutorRepository;
import com.vetly.vetly_java.repository.VeterinarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final AnimalRepository animalRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final TutorRepository tutorRepository;
    private final ConsultaMapper mapper;

    public ConsultaService(ConsultaRepository consultaRepository, AnimalRepository animalRepository,
                           VeterinarioRepository veterinarioRepository, TutorRepository tutorRepository,
                           ConsultaMapper mapper) {
        this.consultaRepository = consultaRepository;
        this.animalRepository = animalRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.tutorRepository = tutorRepository;
        this.mapper = mapper;
    }

    public ConsultaResponse create(ConsultaRequest request) {
        Usuario usuario = currentUsuario();
        requireRole(usuario, UserRole.TUTOR, "Somente tutores podem agendar consultas");
        Tutor tutor = requireTutor(usuario);

        Animal animal = animalRepository.findById(request.animalId())
                .orElseThrow(() -> new EntityNotFoundException("Animal não encontrado: " + request.animalId()));
        if (!animal.getTutor().getId().equals(tutor.getId())) {
            throw new AccessDeniedException("Animal não pertence ao tutor autenticado");
        }

        Veterinario veterinario = veterinarioRepository.findById(request.veterinarioId())
                .orElseThrow(() -> new EntityNotFoundException("Veterinário não encontrado: " + request.veterinarioId()));

        Consulta consulta = mapper.consultaRequestToConsulta(request, animal, veterinario);
        return mapper.consultaToConsultaResponse(consultaRepository.save(consulta));
    }

    public ConsultaResponse read(UUID id) {
        Consulta consulta = findConsultaVisivel(id);
        return mapper.consultaToConsultaResponse(consulta);
    }

    public Page<ConsultaResponse> read(Pageable pageable) {
        Usuario usuario = currentUsuario();
        if (usuario.getRole() == UserRole.TUTOR) {
            Tutor tutor = requireTutor(usuario);
            return consultaRepository.findByAnimal_Tutor(tutor, pageable).map(mapper::consultaToConsultaResponse);
        }
        if (usuario.getRole() == UserRole.VETERINARIO) {
            Veterinario veterinario = requireVeterinario(usuario);
            return consultaRepository.findByVeterinario(veterinario, pageable).map(mapper::consultaToConsultaResponse);
        }
        throw new AccessDeniedException("Somente tutores ou veterinários podem listar consultas");
    }

    public ConsultaResponse reagendar(UUID id, ConsultaReagendarRequest request) {
        Consulta consulta = findConsultaDoTutor(id);
        if (consulta.getStatus() != StatusConsulta.AGENDADA) {
            throw new IllegalArgumentException("Só é possível reagendar consultas com status AGENDADA");
        }
        consulta.setDataHora(request.dataHora());
        return mapper.consultaToConsultaResponse(consultaRepository.save(consulta));
    }

    public ConsultaResponse cancelar(UUID id) {
        Consulta consulta = findConsultaDoTutor(id);
        transicionar(consulta, StatusConsulta.CANCELADA);
        return mapper.consultaToConsultaResponse(consultaRepository.save(consulta));
    }

    public ConsultaResponse realizar(UUID id) {
        Consulta consulta = findConsultaDoVeterinario(id);
        transicionar(consulta, StatusConsulta.REALIZADA);
        return mapper.consultaToConsultaResponse(consultaRepository.save(consulta));
    }

    public ConsultaResponse marcarNaoCompareceu(UUID id) {
        Consulta consulta = findConsultaDoVeterinario(id);
        transicionar(consulta, StatusConsulta.NAO_COMPARECEU);
        return mapper.consultaToConsultaResponse(consultaRepository.save(consulta));
    }

    private void transicionar(Consulta consulta, StatusConsulta destino) {
        if (consulta.getStatus() != StatusConsulta.AGENDADA) {
            throw new IllegalArgumentException(
                    "Transição inválida: consulta está em " + consulta.getStatus() + ", não é possível ir para " + destino);
        }
        consulta.setStatus(destino);
    }

    private Consulta findConsultaVisivel(UUID id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada: " + id));

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
            throw new AccessDeniedException("Somente tutores ou veterinários podem acessar consultas");
        }
        return consulta;
    }

    private Consulta findConsultaDoTutor(UUID id) {
        Usuario usuario = currentUsuario();
        requireRole(usuario, UserRole.TUTOR, "Somente o tutor dono do animal pode realizar esta ação");
        Tutor tutor = requireTutor(usuario);

        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada: " + id));
        if (!consulta.getAnimal().getTutor().getId().equals(tutor.getId())) {
            throw new AccessDeniedException("Consulta não pertence ao tutor autenticado");
        }
        return consulta;
    }

    private Consulta findConsultaDoVeterinario(UUID id) {
        Usuario usuario = currentUsuario();
        requireRole(usuario, UserRole.VETERINARIO, "Somente o veterinário responsável pode realizar esta ação");
        Veterinario veterinario = requireVeterinario(usuario);

        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada: " + id));
        if (!consulta.getVeterinario().getId().equals(veterinario.getId())) {
            throw new AccessDeniedException("Consulta não pertence ao veterinário autenticado");
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
