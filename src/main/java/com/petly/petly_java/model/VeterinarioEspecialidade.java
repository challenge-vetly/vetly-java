package com.petly.petly_java.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
        name = "TB_VETERINARIO_ESPECIALIDADE",
        uniqueConstraints = @UniqueConstraint(
                name = "TB_VETERINARIO_ESPCD_UN",
                columnNames = {"TB_VETERINARIO_ID_VETERINARIO", "TB_ESPCD_VET_ID_ESPCD_VET"}
        )
)
public class VeterinarioEspecialidade {

    @Id
    @Column(name = "ID_ESPECIALIDADE_VETERINARIO")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "TB_VETERINARIO_ID_VETERINARIO", nullable = false)
    private Veterinario veterinario;

    @ManyToOne
    @JoinColumn(name = "TB_ESPCD_VET_ID_ESPCD_VET", nullable = false)
    private EspecialidadeVet especialidade;

    public VeterinarioEspecialidade(UUID id, Veterinario veterinario, EspecialidadeVet especialidade) {
        this.id = id;
        this.veterinario = veterinario;
        this.especialidade = especialidade;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
    }

    public EspecialidadeVet getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(EspecialidadeVet especialidade) {
        this.especialidade = especialidade;
    }
}
