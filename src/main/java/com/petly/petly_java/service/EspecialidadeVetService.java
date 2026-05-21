package com.petly.petly_java.service;

import com.petly.petly_java.dto.EspecialidadeVetLista;
import com.petly.petly_java.dto.EspecialidadeVetRequest;
import com.petly.petly_java.mapper.EspecialidadeVetMapper;
import com.petly.petly_java.model.EspecialidadeVet;
import com.petly.petly_java.repository.EspecialidadeVetRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;
import java.util.UUID;

@Service
public class EspecialidadeVetService {
    private final EspecialidadeVetRepository especialidadeVetRepository;
    private final EspecialidadeVetMapper mapper;

    @Autowired
    public EspecialidadeVetService(EspecialidadeVetRepository especialidadeVetRepository, EspecialidadeVetMapper especialidadeVetMapper, EspecialidadeVetMapper mapper) {
        this.especialidadeVetRepository = especialidadeVetRepository;
        this.mapper = mapper;
    }

    public EspecialidadeVet create(EspecialidadeVetRequest req) {
        EspecialidadeVet especialidadeVet = mapper.especialidadeVetRequestToEspecialidadeVet(req);
//        System.out.println("ID DO MODEL MAPEADO NO SERVICE.CREATE: " + especialidadeVet.getId());
//        EspecialidadeVet especialidadeVet = new EspecialidadeVet();
//        BeanUtils.copyProperties(req, especialidadeVet);
        return especialidadeVetRepository.save(especialidadeVet);
    }

    public EspecialidadeVet readById(String id) {
        Optional<EspecialidadeVet> especialidadeVet = especialidadeVetRepository.findById(id);
        return especialidadeVet.orElse(null);
    }

    public Page<EspecialidadeVetLista> read(Pageable pageable) {
        return especialidadeVetRepository
                .findAll(pageable)
                .map(mapper::especialidadeVetToEspecialidadeVetLista);
    }

    public EspecialidadeVet update(EspecialidadeVet especialidadeVet) {
        return especialidadeVetRepository.save(especialidadeVet);
    }

    public void delete(String id) {
        especialidadeVetRepository.deleteById(id);
    }
}
