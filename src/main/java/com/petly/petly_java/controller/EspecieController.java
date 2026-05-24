package com.petly.petly_java.controller;

import com.petly.petly_java.dto.EspecieRequest;
import com.petly.petly_java.model.Especie;
import com.petly.petly_java.service.EspecieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/especies")
public class EspecieController {
    private final EspecieService especieService;

    @Autowired
    public EspecieController(EspecieService especieService) {
        this.especieService = especieService;
    }

    @PostMapping
    public ResponseEntity<Especie> createEspecie(@Valid @RequestBody EspecieRequest request){
        Especie especie = especieService.create(request);

        return new ResponseEntity<>(especie, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Especie> readEspecie(@Valid @PathVariable String id) {
        Especie especie = especieService.read(id);
        if (especie == null) {
            return new ResponseEntity<>(especie, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(especie, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Especie>> readEspecie() { // Nao usamos paginacao aqui pq a ideia e ter um numero fixo de especies
        var especies = especieService.read();
        return new ResponseEntity<>(especies, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Especie> updateEspecie(@Valid @RequestBody Especie request) {
        Especie especieExistente = especieService.read(request.getId());
        if (especieExistente == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Especie especieAtualizado = especieService.update(request);
        return new ResponseEntity<>(especieAtualizado, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEspecie(@PathVariable String id) {
        especieService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
