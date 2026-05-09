package com.petly.petly_java.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "TB_VETERINARIO")
public class Veterinario {

    @Id
    @GeneratedValue
    @Column(name = "id_veterinario")
    private UUID id;

    @Column(name = "nm_veterinario", nullable = false)
    private String nome;

    @Column(name = "crmv_veterinario", unique = true, nullable = false)
    private String crmv;

    @Column(name = "cpf_veterinario", unique = true, nullable = false)
    private String cpf;

    @Column(name = "telefone")
    private String telefone;

    @Column(name = "flg_atv_veterinario")
    private boolean isAtivo;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "uf_atuacao", nullable = false)
    @Enumerated(EnumType.STRING)
    private UF ufAtuacao;

    @Column(name = "espec_veterinario", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<Especialidade> especialidades;

    @Column(name = "esp_atend_veterinario", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<Especie> especiesAtendidas;

    @OneToMany
    private List<Disponibilidade> disponibilidades;

    public Veterinario(UUID id, String nome, String crmv, String cpf, String telefone, boolean isAtivo, String email, UF ufAtuacao, List<Especialidade> especialidades, List<Especie> especiesAtendidas, List<Disponibilidade> disponibilidades) {
        this.id = id;
        this.nome = nome;
        this.crmv = crmv;
        this.cpf = cpf;
        this.telefone = telefone;
        this.isAtivo = isAtivo;
        this.email = email;
        this.ufAtuacao = ufAtuacao;
        this.especialidades = especialidades;
        this.especiesAtendidas = especiesAtendidas;
        this.disponibilidades = disponibilidades;
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

    public String getCrmv() {
        return crmv;
    }

    public void setCrmv(String crmv) {
        this.crmv = crmv;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public boolean isAtivo() {
        return isAtivo;
    }

    public void setAtivo(boolean ativo) {
        isAtivo = ativo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UF getUfAtuacao() {
        return ufAtuacao;
    }

    public void setUfAtuacao(UF ufAtuacao) {
        this.ufAtuacao = ufAtuacao;
    }

    public List<Especialidade> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(List<Especialidade> especialidades) {
        this.especialidades = especialidades;
    }

    public List<Especie> getEspeciesAtendidas() {
        return especiesAtendidas;
    }

    public void setEspeciesAtendidas(List<Especie> especiesAtendidas) {
        this.especiesAtendidas = especiesAtendidas;
    }

    public List<Disponibilidade> getDisponibilidades() {
        return disponibilidades;
    }

    public void setDisponibilidades(List<Disponibilidade> disponibilidades) {
        this.disponibilidades = disponibilidades;
    }
}