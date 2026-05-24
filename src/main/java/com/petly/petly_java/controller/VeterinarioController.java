package com.petly.petly_java.controller;

import com.petly.petly_java.dto.VeterinarioLista;
import com.petly.petly_java.dto.VeterinarioResponse;
import com.petly.petly_java.service.VeterinarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/veterinarios")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;
    private final PagedResourcesAssembler<VeterinarioLista> assembler;

    public VeterinarioController(VeterinarioService veterinarioService,
                                 PagedResourcesAssembler<VeterinarioLista> assembler) {
        this.veterinarioService = veterinarioService;
        this.assembler = assembler;
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioResponse> readVeterinario(@PathVariable String id) {
        VeterinarioResponse vet = veterinarioService.read(id);
        if (vet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(vet, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<VeterinarioLista>> readVeterinariosByEspecialidade(
            @RequestParam(required = false) String especialidade,
            @RequestParam(defaultValue = "0") Integer pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber, 5, Sort.by("pessoa.nome").ascending());
        if (especialidade == null) {
            Page<VeterinarioLista> veterinarios  = veterinarioService.readVeterinario(pageable);

            if (veterinarios.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(veterinarios, HttpStatus.OK);
        } else {
            Page<VeterinarioLista> veterinarios = veterinarioService.read(especialidade, pageable);

            if (veterinarios.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(veterinarios, HttpStatus.OK);
        }
    }
}