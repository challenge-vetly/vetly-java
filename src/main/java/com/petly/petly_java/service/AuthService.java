package com.petly.petly_java.service;

import com.petly.petly_java.repository.UsuarioRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        UserDetails userDetails = usuarioRepository.findByEmail(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException("Usuario nao encontrado: " + username);
        }
        return userDetails;
    }
}
