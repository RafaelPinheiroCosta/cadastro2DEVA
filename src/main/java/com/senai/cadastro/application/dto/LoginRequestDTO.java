package com.senai.cadastro.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(
        name = "LoginRequest",
        description = "Credenciais de autenticação"
)
public record LoginRequestDTO(
        @Schema(example = "admin@cadastro.local")
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @Schema(example = "Adm@1234")
        @NotBlank(message = "Senha é obrigatória")
        String senha

) {
}