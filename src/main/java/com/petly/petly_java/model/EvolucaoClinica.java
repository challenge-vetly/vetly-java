package com.petly.petly_java.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class EvolucaoClinica {
    // Evolucao Clinica tem as anotacoes do medico geradas na consulta
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private Prontuario prontuario;

    @OneToOne
    private Consulta consulta;

    @Column(length = 10000)
    private String anotacoes;

    public EvolucaoClinica(UUID id, Prontuario prontuario, Consulta consulta, String anotacoes) {
        this.id = id;
        this.prontuario = prontuario;
        this.consulta = consulta;
        this.anotacoes = anotacoes;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Prontuario getProntuario() {
        return prontuario;
    }

    public void setProntuario(Prontuario prontuario) {
        this.prontuario = prontuario;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

    public String getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }
}
