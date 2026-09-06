package com.vetly.vetly_java.controller;

import com.vetly.vetly_java.dto.EvolucaoClinicaResponse;
import com.vetly.vetly_java.dto.LogAcessoProntuarioResponse;
import com.vetly.vetly_java.model.UserRole;
import com.vetly.vetly_java.model.Usuario;
import com.vetly.vetly_java.service.AcessoProntuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/animais/{animalId}")
public class ColmeiaController {

    private final AcessoProntuarioService acessoProntuarioService;

    public ColmeiaController(AcessoProntuarioService acessoProntuarioService) {
        this.acessoProntuarioService = acessoProntuarioService;
    }

    @GetMapping("/historico")
    public ResponseEntity<List<EvolucaoClinicaResponse>> readHistorico(
            @PathVariable UUID animalId,
            @RequestParam(required = false) String contexto) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<EvolucaoClinicaResponse> historico = switch (usuario.getRole()) {
            case TUTOR -> acessoProntuarioService.historicoParaTutor(animalId);
            case VETERINARIO -> acessoProntuarioService.historicoParaVeterinario(animalId, contexto);
            default -> throw new AccessDeniedException("Somente tutores ou veterinários acessam o histórico clínico");
        };

        return new ResponseEntity<>(historico, HttpStatus.OK);
    }

    @GetMapping("/logs-acesso")
    public ResponseEntity<Page<LogAcessoProntuarioResponse>> readLogsAcesso(
            @PathVariable UUID animalId,
            @RequestParam(defaultValue = "0") Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 20, Sort.by("dataHoraAcesso").descending());
        return new ResponseEntity<>(acessoProntuarioService.logsDeAcesso(animalId, pageable), HttpStatus.OK);
    }
}
