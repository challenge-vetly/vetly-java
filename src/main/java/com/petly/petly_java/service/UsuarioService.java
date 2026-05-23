package com.petly.petly_java.service;

import com.petly.petly_java.dto.RegisterDTO;
import com.petly.petly_java.dto.UsuarioDTO;
import com.petly.petly_java.dto.UsuarioResponse;
import com.petly.petly_java.mapper.UsuarioMapper;
import com.petly.petly_java.model.Usuario;
import com.petly.petly_java.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioMapper mapper;
    public UsuarioResponse create(UsuarioDTO usuarioDTO) {
        Usuario usuario = mapper.usuarioDTOToUsuario(usuarioDTO);
        usuario.setSenhaHash(new BCryptPasswordEncoder().encode(usuarioDTO.senha()));
        return mapper.usuarioToResponse(usuarioRepository.save(usuario));
    }
}
