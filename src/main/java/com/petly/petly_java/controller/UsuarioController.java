package com.petly.petly_java.controller;

import com.petly.petly_java.dto.UsuarioDTO;
import com.petly.petly_java.dto.UsuarioLista;
import com.petly.petly_java.dto.UsuarioResponse;
import com.petly.petly_java.model.Usuario;
import com.petly.petly_java.service.UsuarioService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> createUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        return new ResponseEntity<>(usuarioService.create(usuarioDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> readUsuario(@PathVariable String id) {
        UsuarioResponse usuarioResponse = usuarioService.read(id);
        return new ResponseEntity<>(usuarioResponse, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Page<UsuarioLista>> readUsuario(@RequestParam(defaultValue = "0") Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 5,  Sort.by("email").ascending());
        Page<UsuarioLista> usuarios = usuarioService.read(pageable);
        if (usuarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }
}
