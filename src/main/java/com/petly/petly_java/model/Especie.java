package com.petly.petly_java.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "TB_ESPECIE",
        uniqueConstraints = @UniqueConstraint(
                name = "TB_ESPECIE_NM_ESPECIE_UN",
                columnNames = {"NM_ESPECIE"}
        ))
public class Especie {

    @Id
    @Column(name = "ID_ESPECIE")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "NM_ESPECIE", nullable = false, unique = true, length = 50)
    @Enumerated(EnumType.STRING)
    private NomeEspecie nome;

    public Especie(UUID id, NomeEspecie nome) {
        this.id = id;
        this.nome = nome;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public NomeEspecie getNome() {
        return nome;
    }

    public void setNome(NomeEspecie nome) {
        this.nome = nome;
    }
}