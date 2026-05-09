package com.petly.petly_java.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Entity
public class Prontuario {
    // Prontuario eh um historico/sumario do animal, por isso tem uma lista de evolucaoCLinica e relacao 1-1 com o animal
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    private Animal animal;

    @OneToMany(mappedBy = "prontuario")
    private List<EvolucaoClinica> evolucoes;

    public Prontuario(UUID id, Animal animal, List<EvolucaoClinica> evolucoes) {
        this.id = id;
        this.animal = animal;
        this.evolucoes = evolucoes;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public List<EvolucaoClinica> getEvolucoes() {
        return evolucoes;
    }

    public void setEvolucoes(List<EvolucaoClinica> evolucoes) {
        this.evolucoes = evolucoes;
    }
}

