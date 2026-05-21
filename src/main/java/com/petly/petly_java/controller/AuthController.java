package com.petly.petly_java.controller;

import com.petly.petly_java.dto.AuthDTO;
import com.petly.petly_java.dto.RegisterDTO;
import com.petly.petly_java.model.Usuario;
import com.petly.petly_java.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UsuarioRepository usuarioRepository;
    private AuthenticationManager authenticationManager;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

//    @PostMapping("/login")
//    public ResponseEntity login(@Valid @RequestBody AuthDTO authDTO) {
//
//    }

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody RegisterDTO registerDTO) {
        if (usuarioRepository.findByEmail(registerDTO.email()) != null) {
            return ResponseEntity.badRequest().build();
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(registerDTO.senha());
        Usuario novoUsuario = new Usuario(registerDTO.email(), registerDTO.role(), 'S', encryptedPassword);
        usuarioRepository.save(novoUsuario);
        return ResponseEntity.ok().build();
    }
}
