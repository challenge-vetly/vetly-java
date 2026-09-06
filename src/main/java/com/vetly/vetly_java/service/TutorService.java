package com.vetly.vetly_java.service;

import com.vetly.vetly_java.dto.TutorConsentimentoRequest;
import com.vetly.vetly_java.dto.TutorConsentimentoResponse;
import com.vetly.vetly_java.model.Tutor;
import com.vetly.vetly_java.model.Usuario;
import com.vetly.vetly_java.model.UserRole;
import com.vetly.vetly_java.repository.TutorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TutorService {

    private final TutorRepository tutorRepository;

    public TutorService(TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

    public TutorConsentimentoResponse lerConsentimento() {
        return toResponse(currentTutor());
    }

    public TutorConsentimentoResponse atualizarConsentimento(TutorConsentimentoRequest request) {
        Tutor tutor = currentTutor();

        if (request.lgpdAceito() && !tutor.isLgpdAceito()) {
            tutor.setDataLgpdAceito(LocalDate.now());
        }
        tutor.setLgpdAceito(request.lgpdAceito() ? "S" : "N");

        if (request.consentimentoRede() && !tutor.isConsentimentoRede()) {
            tutor.setDataConsentimentoRede(LocalDate.now());
        } else if (!request.consentimentoRede()) {
            tutor.setDataConsentimentoRede(null);
        }
        tutor.setConsentimentoRede(request.consentimentoRede() ? "S" : "N");

        return toResponse(tutorRepository.save(tutor));
    }

    private TutorConsentimentoResponse toResponse(Tutor tutor) {
        return new TutorConsentimentoResponse(
                tutor.isLgpdAceito(),
                tutor.getDataLgpdAceito(),
                tutor.isConsentimentoRede(),
                tutor.getDataConsentimentoRede()
        );
    }

    private Tutor currentTutor() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (usuario.getRole() != UserRole.TUTOR) {
            throw new AccessDeniedException("Somente tutores possuem consentimento LGPD/rede");
        }
        Tutor tutor = tutorRepository.findByUsuario(usuario);
        if (tutor == null) {
            throw new EntityNotFoundException("Tutor não encontrado para o usuário autenticado");
        }
        return tutor;
    }
}
