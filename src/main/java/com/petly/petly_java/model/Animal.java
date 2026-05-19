package com.petly.petly_java.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tb_animal")
public class Animal {
    @Id
    @Column(name = "ID_ANIMAL")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "NM_ANIMAL", nullable = false, length = 80)
    private String nome;

    @Column(name = "RC_ANIMAL", nullable = false, length = 80)
    private String raca;

    @Column(name = "SX_ANIMAL", nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    @Column(name = "DT_NASC_ANIMAL")
    private LocalDate dataNascimento;

    @Column(name = "NR_PESO_ANIMAL", nullable = false, precision = 5, scale = 2)
    private BigDecimal peso;

    @ManyToOne
    @JoinColumn(name = "TB_TUTOR_ID_TUTOR", nullable = false)
    private Tutor tutor;

    @ManyToOne
    @JoinColumn(name = "TB_ESPECIE_ID_ESPECIE", nullable = false)
    private Especie especie;


    public Animal(UUID id, String nome, String raca, Sexo sexo, LocalDate dataNascimento, BigDecimal peso, Tutor tutor, Especie especie) {
        this.id = id;
        this.nome = nome;
        this.raca = raca;
        this.sexo = sexo;
        this.dataNascimento = dataNascimento;
        this.peso = peso;
        this.tutor = tutor;
        this.especie = especie;
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

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }
}
