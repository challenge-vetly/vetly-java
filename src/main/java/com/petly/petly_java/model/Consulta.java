package com.petly.petly_java.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Consulta {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private Animal animal;

    @ManyToOne
    private Veterinario veterinario;

    private LocalDateTime dataHora;

    @OneToOne
    private EvolucaoClinica evolucaoClinica; // A consulta gera uma evolucao clinica, com as anotacoes do medico

    public Consulta(UUID id, Animal animal, Veterinario veterinario, LocalDateTime dataHora, EvolucaoClinica evolucaoClinica) {
        this.id = id;
        this.animal = animal;
        this.veterinario = veterinario;
        this.dataHora = dataHora;
        this.evolucaoClinica = evolucaoClinica;
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

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public EvolucaoClinica getEvolucaoClinica() {
        return evolucaoClinica;
    }

    public void setEvolucaoClinica(EvolucaoClinica evolucaoClinica) {
        this.evolucaoClinica = evolucaoClinica;
    }
}