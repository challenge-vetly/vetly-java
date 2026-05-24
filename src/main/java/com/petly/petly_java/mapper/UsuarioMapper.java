package com.petly.petly_java.mapper;

import com.petly.petly_java.controller.UsuarioController;
import com.petly.petly_java.dto.UsuarioDTO;
import com.petly.petly_java.dto.UsuarioLista;
import com.petly.petly_java.dto.UsuarioResponse;
import com.petly.petly_java.model.UserRole;
import com.petly.petly_java.model.Usuario;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UsuarioMapper {
    public UsuarioResponse usuarioToResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getEmail(), usuario.getRole(), usuario.getFlagAtivo());
    }

    public Usuario usuarioDTOToUsuario(UsuarioDTO usuarioDTO) {
        String id = UUID.randomUUID().toString();
        return new Usuario(id, usuarioDTO.email(), UserRole.valueOf(usuarioDTO.role()), usuarioDTO.flagAtivo(), usuarioDTO.senha());
    }

    public UsuarioLista usuarioToUsuarioLista(Usuario usuario) {
        Link link = linkTo(methodOn(UsuarioController.class).readUsuario(usuario.getId())).withRel("Detalhes usuario");
        return new UsuarioLista(usuario.getEmail(), link);
    }
}
