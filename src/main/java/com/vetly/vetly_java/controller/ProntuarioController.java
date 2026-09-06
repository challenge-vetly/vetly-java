package com.vetly.vetly_java.controller;

import com.vetly.vetly_java.dto.ProntuarioCorrecaoRequest;
import com.vetly.vetly_java.dto.ProntuarioRequest;
import com.vetly.vetly_java.dto.ProntuarioResponse;
import com.vetly.vetly_java.service.ProntuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/animais/{animalId}/prontuario")
public class ProntuarioController {

    private final ProntuarioService prontuarioService;

    public ProntuarioController(ProntuarioService prontuarioService) {
        this.prontuarioService = prontuarioService;
    }

    @PostMapping
    public ResponseEntity<ProntuarioResponse> createProntuario(
            @PathVariable UUID animalId, @Valid @RequestBody ProntuarioRequest request) {
        return new ResponseEntity<>(prontuarioService.criar(animalId, request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProntuarioResponse>> readProntuario(@PathVariable UUID animalId) {
        return new ResponseEntity<>(prontuarioService.ler(animalId), HttpStatus.OK);
    }

    @PostMapping("/correcoes")
    public ResponseEntity<ProntuarioResponse> corrigirProntuario(
            @PathVariable UUID animalId, @Valid @RequestBody ProntuarioCorrecaoRequest request) {
        return new ResponseEntity<>(prontuarioService.corrigir(animalId, request), HttpStatus.CREATED);
    }
}
