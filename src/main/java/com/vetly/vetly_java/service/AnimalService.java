package com.vetly.vetly_java.service;

import com.vetly.vetly_java.dto.AnimalRequest;
import com.vetly.vetly_java.dto.AnimalResponse;
import com.vetly.vetly_java.mapper.AnimalMapper;
import com.vetly.vetly_java.model.Animal;
import com.vetly.vetly_java.model.Especie;
import com.vetly.vetly_java.model.Tutor;
import com.vetly.vetly_java.model.Usuario;
import com.vetly.vetly_java.model.UserRole;
import com.vetly.vetly_java.repository.AnimalRepository;
import com.vetly.vetly_java.repository.EspecieRepository;
import com.vetly.vetly_java.repository.TutorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final TutorRepository tutorRepository;
    private final EspecieRepository especieRepository;
    private final AnimalMapper mapper;

    public AnimalService(AnimalRepository animalRepository, TutorRepository tutorRepository,
                         EspecieRepository especieRepository, AnimalMapper mapper) {
        this.animalRepository = animalRepository;
        this.tutorRepository = tutorRepository;
        this.especieRepository = especieRepository;
        this.mapper = mapper;
    }

    public AnimalResponse create(AnimalRequest request) {
        Tutor tutor = currentTutor();
        Especie especie = findEspecie(request.especieId());

        Animal animal = mapper.animalRequestToAnimal(request, tutor, especie);
        return mapper.animalToAnimalResponse(animalRepository.save(animal));
    }

    public AnimalResponse read(UUID id) {
        Animal animal = findOwnedAnimal(id);
        return mapper.animalToAnimalResponse(animal);
    }

    public Page<AnimalResponse> read(Pageable pageable) {
        Tutor tutor = currentTutor();
        return animalRepository.findByTutor(tutor, pageable)
                .map(mapper::animalToAnimalResponse);
    }

    public AnimalResponse update(UUID id, AnimalRequest request) {
        Animal animal = findOwnedAnimal(id);
        Especie especie = findEspecie(request.especieId());

        mapper.updateAnimalFromRequest(animal, request, especie);
        return mapper.animalToAnimalResponse(animalRepository.save(animal));
    }

    public void delete(UUID id) {
        Animal animal = findOwnedAnimal(id);
        animalRepository.delete(animal);
    }

    private Especie findEspecie(String especieId) {
        return especieRepository.findById(especieId)
                .orElseThrow(() -> new EntityNotFoundException("Espécie não encontrada: " + especieId));
    }

    private Animal findOwnedAnimal(UUID id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Animal não encontrado: " + id));

        Tutor tutor = currentTutor();
        if (!animal.getTutor().getId().equals(tutor.getId())) {
            throw new AccessDeniedException("Animal não pertence ao tutor autenticado");
        }
        return animal;
    }

    private Tutor currentTutor() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (usuario.getRole() != UserRole.TUTOR) {
            throw new AccessDeniedException("Somente tutores podem gerenciar animais");
        }
        Tutor tutor = tutorRepository.findByUsuario(usuario);
        if (tutor == null) {
            throw new EntityNotFoundException("Tutor não encontrado para o usuário autenticado");
        }
        return tutor;
    }
}
