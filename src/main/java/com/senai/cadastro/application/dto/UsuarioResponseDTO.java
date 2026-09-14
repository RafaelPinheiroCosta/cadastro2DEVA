package com.senai.cadastro.application.dto;

import com.senai.cadastro.domain.entity.Perfil;
import com.senai.cadastro.domain.entity.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(
        name = "UsuarioResponse",
        description =
                "Representação pública do usuário. A senha nunca é retornada."
)
public record UsuarioResponseDTO(

        @Schema(
                example =
                        "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,

        @Schema(
                example = "João da Silva"
        )
        String nome,

        @Schema(
                example = "11144477735"
        )
        String cpf,

        @Schema(
                example = "usuario@email.com"
        )
        String email,

        @Schema(
                example = "USER"
        )
        Perfil perfil

) {

    public static UsuarioResponseDTO fromEntity(
            Usuario usuario
    ) {

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getEmail(),
                usuario.getPerfil()
        );
    }
}