package com.vetly.vetly_java.controller;

import com.vetly.vetly_java.dto.ConsultaReagendarRequest;
import com.vetly.vetly_java.dto.ConsultaRequest;
import com.vetly.vetly_java.dto.ConsultaResponse;
import com.vetly.vetly_java.service.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @PostMapping
    public ResponseEntity<ConsultaResponse> createConsulta(@Valid @RequestBody ConsultaRequest request) {
        ConsultaResponse consulta = consultaService.create(request);
        return new ResponseEntity<>(consulta, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponse> readConsulta(@PathVariable UUID id) {
        return new ResponseEntity<>(consultaService.read(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<ConsultaResponse>> readConsultas(@RequestParam(defaultValue = "0") Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by("dataHora").descending());
        return new ResponseEntity<>(consultaService.read(pageable), HttpStatus.OK);
    }

    @PatchMapping("/{id}/reagendar")
    public ResponseEntity<ConsultaResponse> reagendarConsulta(@PathVariable UUID id, @Valid @RequestBody ConsultaReagendarRequest request) {
        return new ResponseEntity<>(consultaService.reagendar(id, request), HttpStatus.OK);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ConsultaResponse> cancelarConsulta(@PathVariable UUID id) {
        return new ResponseEntity<>(consultaService.cancelar(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}/realizar")
    public ResponseEntity<ConsultaResponse> realizarConsulta(@PathVariable UUID id) {
        return new ResponseEntity<>(consultaService.realizar(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}/nao-compareceu")
    public ResponseEntity<ConsultaResponse> marcarNaoCompareceu(@PathVariable UUID id) {
        return new ResponseEntity<>(consultaService.marcarNaoCompareceu(id), HttpStatus.OK);
    }
}
