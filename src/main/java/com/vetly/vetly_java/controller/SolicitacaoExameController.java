package com.vetly.vetly_java.controller;

import com.vetly.vetly_java.dto.*;
import com.vetly.vetly_java.service.SolicitacaoExameService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class SolicitacaoExameController {

    private final SolicitacaoExameService solicitacaoExameService;

    public SolicitacaoExameController(SolicitacaoExameService solicitacaoExameService) {
        this.solicitacaoExameService = solicitacaoExameService;
    }

    @PostMapping("/consultas/{consultaId}/solicitacoes-exame")
    public ResponseEntity<SolicitacaoExameResponse> createSolicitacaoExame(
            @PathVariable UUID consultaId, @Valid @RequestBody SolicitacaoExameRequest request) {
        SolicitacaoExameResponse solicitacao = solicitacaoExameService.create(consultaId, request);
        return new ResponseEntity<>(solicitacao, HttpStatus.CREATED);
    }

    @GetMapping("/consultas/{consultaId}/solicitacoes-exame")
    public ResponseEntity<SolicitacaoExameResponse> readSolicitacaoExame(@PathVariable UUID consultaId) {
        return new ResponseEntity<>(solicitacaoExameService.readPorConsulta(consultaId), HttpStatus.OK);
    }

    @PatchMapping("/solicitacoes-exame/itens/{itemId}/resultado")
    public ResponseEntity<SolicitacaoExameItemResponse> registrarResultado(
            @PathVariable UUID itemId, @Valid @RequestBody ResultadoExameRequest request) {
        return new ResponseEntity<>(solicitacaoExameService.registrarResultado(itemId, request), HttpStatus.OK);
    }

    @PostMapping("/solicitacoes-exame/itens/{itemId}/anexos")
    public ResponseEntity<AnexoExameResponse> anexarArquivo(
            @PathVariable UUID itemId, @Valid @RequestBody AnexoExameRequest request) {
        AnexoExameResponse anexo = solicitacaoExameService.anexarArquivo(itemId, request);
        return new ResponseEntity<>(anexo, HttpStatus.CREATED);
    }

    @PatchMapping("/solicitacoes-exame/itens/{itemId}/liberar")
    public ResponseEntity<SolicitacaoExameItemResponse> liberarParaResponsavel(@PathVariable UUID itemId) {
        return new ResponseEntity<>(solicitacaoExameService.liberarParaResponsavel(itemId), HttpStatus.OK);
    }

    @PatchMapping("/solicitacoes-exame/itens/{itemId}/cancelar")
    public ResponseEntity<SolicitacaoExameItemResponse> cancelar(@PathVariable UUID itemId) {
        return new ResponseEntity<>(solicitacaoExameService.cancelar(itemId), HttpStatus.OK);
    }
}
