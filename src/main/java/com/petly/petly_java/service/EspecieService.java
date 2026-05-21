package com.petly.petly_java.service;

import com.petly.petly_java.dto.EspecieRequest;
import com.petly.petly_java.mapper.EspecieMapper;
import com.petly.petly_java.model.Especie;
import com.petly.petly_java.repository.EspecieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EspecieService {
    private final EspecieRepository especieRepository;
    private final EspecieMapper mapper;

    @Autowired
    EspecieService(EspecieRepository especieRepository, EspecieMapper mapper){
        this.especieRepository = especieRepository;
        this.mapper = mapper;
    }

    public Especie create(EspecieRequest request) {
        Especie especie = mapper.especieRequestToEspecie(request);
        return especieRepository.save(especie);
    }

    public Especie read(String id) {
        Optional<Especie> especie = especieRepository.findById(id);
        return especie.orElse(null);
    }

    public List<Especie> read() {
        return especieRepository.findAll();
    }

    public Especie update(Especie especie) {
        return especieRepository.save(especie);
    }

    public void delete(String id) {
        especieRepository.deleteById(id);
    }
}
