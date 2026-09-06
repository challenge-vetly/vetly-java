package com.vetly.vetly_java.controller;

import com.vetly.vetly_java.dto.AnimalRequest;
import com.vetly.vetly_java.dto.AnimalResponse;
import com.vetly.vetly_java.service.AnimalService;
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
@RequestMapping("/animais")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @PostMapping
    public ResponseEntity<AnimalResponse> createAnimal(@Valid @RequestBody AnimalRequest request) {
        AnimalResponse animal = animalService.create(request);
        return new ResponseEntity<>(animal, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponse> readAnimal(@PathVariable UUID id) {
        return new ResponseEntity<>(animalService.read(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<AnimalResponse>> readAnimais(@RequestParam(defaultValue = "0") Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by("nome").ascending());
        Page<AnimalResponse> animais = animalService.read(pageable);
        return new ResponseEntity<>(animais, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalResponse> updateAnimal(@PathVariable UUID id, @Valid @RequestBody AnimalRequest request) {
        return new ResponseEntity<>(animalService.update(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable UUID id) {
        animalService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
