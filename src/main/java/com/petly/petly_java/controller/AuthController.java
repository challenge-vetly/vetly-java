package com.petly.petly_java.controller;

import com.petly.petly_java.dto.*;
import com.petly.petly_java.model.Usuario;
import com.petly.petly_java.repository.UsuarioRepository;
import com.petly.petly_java.service.AuthService;
import com.petly.petly_java.service.TokenService;
import com.petly.petly_java.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UsuarioRepository usuarioRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private AuthService authService;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody AuthDTO AuthDTO) {
        try {
            // Gerando um token com o login e senha passados
            var usuarioSenha = new UsernamePasswordAuthenticationToken(AuthDTO.email(), AuthDTO.senha());
            // Autenticando o token
            var auth = this.authenticationManager.authenticate(usuarioSenha);
            var token = tokenService.generateToken((Usuario) auth.getPrincipal());
            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha incorreta");
        } catch (InternalAuthenticationServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno de autenticação");
        }

    }

    @PostMapping("/register/veterinario")
    public ResponseEntity register(@Valid @RequestBody RegisterVeterinarioDTO registerVeterinarioDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerVeterinario(registerVeterinarioDTO));
    }

    @PostMapping("/register/admin")
    public ResponseEntity register(@Valid @RequestBody RegisterAdminDTO registerAdminDTO) {
        if (usuarioRepository.findByEmail(registerAdminDTO.email()) != null) {
            return ResponseEntity.badRequest().build();
        }
        UsuarioDTO usuarioDTO = new UsuarioDTO(registerAdminDTO.email(), "ADMIN", "S", registerAdminDTO.senha());
        usuarioService.create(usuarioDTO);
        return ResponseEntity.ok().build();
    }

}
