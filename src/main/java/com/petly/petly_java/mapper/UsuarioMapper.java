package com.petly.petly_java.mapper;

import com.petly.petly_java.dto.RegisterDTO;
import com.petly.petly_java.dto.UsuarioDTO;
import com.petly.petly_java.dto.UsuarioResponse;
import com.petly.petly_java.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UsuarioMapper {
    public UsuarioResponse usuarioToResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getEmail(), usuario.getRole(), usuario.getFlagAtivo());
    }

    public Usuario usuarioDTOToUsuario(UsuarioDTO usuarioDTO) {
        String id = UUID.randomUUID().toString();
        return new Usuario(id, usuarioDTO.email(), usuarioDTO.role(), usuarioDTO.flagAtivo(), usuarioDTO.senha());
    }
}
