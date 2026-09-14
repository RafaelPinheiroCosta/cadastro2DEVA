package com.senai.cadastro.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "LoginResponse",
        description ="Resposta gerada quando a autenticação é realizada com sucesso"
)
public record LoginResponseDTO(
        @Schema(description = "JSON Web Token",example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,

        @Schema(example = "Bearer")
        String tipo,

        @Schema(description ="Tempo restante de validade em segundos",example = "3600")
        long expiresIn,

        UsuarioResponseDTO usuario

) {
}