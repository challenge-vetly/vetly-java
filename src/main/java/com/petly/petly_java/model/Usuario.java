package com.petly.petly_java.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "TB_USUARIO")
public class Usuario {

    @Id
    @Column(name = "ID_USUARIO")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "EM_USUARIO", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "RL_USUARIO", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "FL_ATV_USUARIO", nullable = false, length = 1)
    private Character flagAtivo;


    @Column(name = "SEN_HASH_USUARIO", nullable = false, length = 255)
    private String senhaHash;

    public Usuario(String email, UserRole role, Character flagAtivo, String senhaHash) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.flagAtivo = flagAtivo;
        this.senhaHash = senhaHash;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Character getFlagAtivo() {
        return flagAtivo;
    }

    public void setFlagAtivo(Character flagAtivo) {
        this.flagAtivo = flagAtivo;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }
}