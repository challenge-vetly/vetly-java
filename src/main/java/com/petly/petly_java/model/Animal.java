package com.petly.petly_java.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tb_animal")
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_animal", nullable = false)
    private UUID id;

    @Column(name = "nm_animal", nullable = false)
    private String nome;

    @Column(name = "esp_animal", nullable = false)
    @Enumerated(EnumType.STRING)
    private Especie especie;

    @Column(name = "rc_animal", nullable = false)
    private String raca;

    @Column(name = "idd_animal", nullable = false)
    private int idade;

    @OneToOne
    private Prontuario prontuario;

    public Animal(UUID id, String nome, Especie especie, String raca, int idade, Prontuario prontuario) {
        this.id = id;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.idade = idade;
        this.prontuario = prontuario;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public Prontuario getProntuario() {
        return prontuario;
    }

    public void setProntuario(Prontuario prontuario) {
        this.prontuario = prontuario;
    }
}
