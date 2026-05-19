package com.petly.petly_java.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_LOG_ERRO")
public class LogErro {

    @Id
    @Column(name = "ID_LOG")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "NM_PROCEDURE", nullable = false, length = 100)
    private String nomeProcedure;

    @Column(name = "NM_USUARIO", nullable = false, length = 100)
    private String nomeUsuario;

    @Column(name = "DT_OCORRENCIA", nullable = false)
    private LocalDateTime dataOcorrencia;

    @Column(name = "CD_ERRO", nullable = false)
    private Integer codigoErro;

    @Column(name = "DS_MENSAGEM", nullable = false, length = 4000)
    private String mensagem;

    public LogErro(UUID id, String nomeProcedure, String nomeUsuario, LocalDateTime dataOcorrencia, Integer codigoErro, String mensagem) {
        this.id = id;
        this.nomeProcedure = nomeProcedure;
        this.nomeUsuario = nomeUsuario;
        this.dataOcorrencia = dataOcorrencia;
        this.codigoErro = codigoErro;
        this.mensagem = mensagem;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNomeProcedure() {
        return nomeProcedure;
    }

    public void setNomeProcedure(String nomeProcedure) {
        this.nomeProcedure = nomeProcedure;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public LocalDateTime getDataOcorrencia() {
        return dataOcorrencia;
    }

    public void setDataOcorrencia(LocalDateTime dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }

    public Integer getCodigoErro() {
        return codigoErro;
    }

    public void setCodigoErro(Integer codigoErro) {
        this.codigoErro = codigoErro;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
