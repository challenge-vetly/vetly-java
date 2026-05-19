package com.petly.petly_java.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "TB_PRONTUARIO")
public class Prontuario {

    @Id
    @Column(name = "ID_PRONTUARIO")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "DT_UPD_PRONTURARIO", nullable = false)
    private LocalDate dataUltimaAtualizacao;

    @OneToOne
    @JoinColumn(name = "TB_ANIMAL_ID_ANIMAL", nullable = false, unique = true)
    private Animal animal;

    public Prontuario(UUID id, LocalDate dataUltimaAtualizacao, Animal animal) {
        this.id = id;
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
        this.animal = animal;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }

    public void setDataUltimaAtualizacao(LocalDate dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }
}
