package com.petly.petly_java.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "TB_TUTOR")
public class Tutor {

    @Id
    @Column(name = "ID_TUTOR")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "TB_USUARIO_ID_USUARIO", nullable = false)
    private Usuario usuario;

    @OneToOne
    @JoinColumn(name = "TB_PESSOA_ID_PESSOA", nullable = false)
    private Pessoa pessoa;

    public Tutor(UUID id, Usuario usuario, Pessoa pessoa) {
        this.id = id;
        this.usuario = usuario;
        this.pessoa = pessoa;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }
}