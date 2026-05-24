package com.petly.petly_java.controller;

import com.petly.petly_java.dto.EspecialidadeVetLista;
import com.petly.petly_java.dto.EspecialidadeVetRequest;
import com.petly.petly_java.model.EspecialidadeVet;
import com.petly.petly_java.service.EspecialidadeVetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/especialidades")
public class EspecialidadeVetController {
   private final EspecialidadeVetService especialidadeVetService;

   @Autowired
   public EspecialidadeVetController(EspecialidadeVetService especialidadeVetService) {
      this.especialidadeVetService = especialidadeVetService;
   }

   @PostMapping
    public ResponseEntity<EspecialidadeVet> createEspecialidadeVet(@Valid @RequestBody EspecialidadeVetRequest req) {
       EspecialidadeVet especialidadeVet = especialidadeVetService.create(req);
       return new ResponseEntity<>(especialidadeVet, HttpStatus.CREATED);
   }

   @GetMapping("/{id}")
   public ResponseEntity<EspecialidadeVet> readEspecialidadeVetById(@PathVariable String id) {
      EspecialidadeVet especialidadeVet = especialidadeVetService.readById(id);
      if (especialidadeVet == null) {
         return new ResponseEntity<>(especialidadeVet, HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(especialidadeVet, HttpStatus.OK);
   }

   @GetMapping
   public ResponseEntity<Page<EspecialidadeVetLista>> readEspecialidadeVet(@RequestParam(defaultValue = "0") Integer pageNumber) {
      Pageable pageable = PageRequest.of(pageNumber, 2, Sort.by("nome").ascending());
      Page<EspecialidadeVetLista> especialidadeVets = especialidadeVetService.read(pageable);
      if (especialidadeVets.isEmpty()) {
         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
      return new ResponseEntity<>(especialidadeVets, HttpStatus.OK);
   }

   @PutMapping
   public ResponseEntity<EspecialidadeVet> updateEspecialidadeVet(@RequestBody EspecialidadeVet especialidadeVet) {
      EspecialidadeVet especialidadeVetExistente = especialidadeVetService.readById(especialidadeVet.getId());
      if (especialidadeVetExistente == null) {
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
      EspecialidadeVet especialidadeVetAtualizada = especialidadeVetService.update(especialidadeVet);
      return new ResponseEntity<>(especialidadeVetAtualizada, HttpStatus.CREATED);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deleteEspecialidadeVet(@PathVariable String id) {
      especialidadeVetService.delete(id);
      return new ResponseEntity<>(HttpStatus.OK);
   }
}
