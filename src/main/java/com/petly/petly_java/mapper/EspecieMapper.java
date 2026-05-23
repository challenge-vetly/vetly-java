package com.petly.petly_java.mapper;

import com.petly.petly_java.dto.EspecieRequest;
import com.petly.petly_java.model.Especie;
import com.petly.petly_java.model.NomeEspecie;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EspecieMapper {
    public Especie especieRequestToEspecie(EspecieRequest request) {
        Especie especie = new Especie();
        especie.setId(UUID.randomUUID().toString());
        System.out.println("UUID CRIADO NO MAPPER: " + especie.getId());
        especie.setNome(NomeEspecie.valueOf(request.nome()));
        return especie;
    }
}
