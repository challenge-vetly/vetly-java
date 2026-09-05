package com.vetly.vetly_java.service;

import com.vetly.vetly_java.dto.UsuarioDTO;
import com.vetly.vetly_java.dto.UsuarioLista;
import com.vetly.vetly_java.dto.UsuarioResponse;
import com.vetly.vetly_java.mapper.UsuarioMapper;
import com.vetly.vetly_java.model.Usuario;
import com.vetly.vetly_java.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper mapper;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper mapper) {
        this.mapper = mapper;
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponse create(UsuarioDTO usuarioDTO) {
        Usuario usuario = mapper.usuarioDTOToUsuario(usuarioDTO);
        usuario.setSenhaHash(new BCryptPasswordEncoder().encode(usuarioDTO.senha()));
        return mapper.usuarioToResponse(usuarioRepository.save(usuario));
    }

    public UsuarioResponse read(String id) {
        return usuarioRepository.findById(id)
                .map(mapper::usuarioToResponse)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + id));
    }

    public Page<UsuarioLista> read(Pageable pageable) {
        return usuarioRepository
                .findAll(pageable)
                .map(mapper::usuarioToUsuarioLista);
    }
}
