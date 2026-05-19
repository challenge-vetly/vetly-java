package com.petly.petly_java.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class EvolucaoClinica {
    @Id
    @Column(name = "ID_EVOLUCAO_CLINICA")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ANT_EVOLUCAO_CLINICA", nullable = false, length = 2000)
    private String anotacoes;

    @OneToOne
    @JoinColumn(name = "TB_CONSULTA_ID_CONSULTA", nullable = false, unique = true)
    private Consulta consulta;

    public EvolucaoClinica(UUID id, String anotacoes, Consulta consulta) {
        this.id = id;
        this.anotacoes = anotacoes;
        this.consulta = consulta;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }
}
