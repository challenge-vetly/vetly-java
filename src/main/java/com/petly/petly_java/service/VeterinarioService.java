package com.petly.petly_java.service;

import com.petly.petly_java.dto.VeterinarioLista;
import com.petly.petly_java.dto.VeterinarioResponse;
import com.petly.petly_java.mapper.VeterinarioMapper;
import com.petly.petly_java.model.NomeEspecialidade;
import com.petly.petly_java.repository.VeterinarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final VeterinarioMapper mapper;

    public VeterinarioService(VeterinarioRepository veterinarioRepository, VeterinarioMapper mapper) {
        this.veterinarioRepository = veterinarioRepository;
        this.mapper = mapper;
    }

    public VeterinarioResponse read(String id) {
        return veterinarioRepository.findById(id)
                .map(mapper::veterinarioToVeterinarioResponse)
                .orElse(null);
    }

    public Page<VeterinarioLista> read(String especialidadeStr, Pageable pageable) {
        NomeEspecialidade especialidade;
        try {
            especialidade = NomeEspecialidade.valueOf(especialidadeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Especialidade inválida: " + especialidadeStr
                    + ". Valores aceitos: " + java.util.Arrays.toString(NomeEspecialidade.values()));
        }

        return veterinarioRepository
                .findByEspecialidade(especialidade, pageable)
                .map(mapper::veterinarioToVeterinarioLista);
    }

    public Page<VeterinarioLista> readVeterinario(Pageable pageable) {
        return veterinarioRepository
                .findAll(pageable)
                .map(mapper::veterinarioToVeterinarioLista);
    }
}