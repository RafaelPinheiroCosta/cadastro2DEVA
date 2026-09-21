package com.senai.cadastro.application.dto;

import com.senai.cadastro.domain.entity.Perfil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        name = "PerfilUpdate",
        description = "Alteração administrativa do perfil de um usuário"
)
public record PerfilUpdateDTO(
        @Schema(description ="Novo perfil do usuário",example = "ADMIN")
        @NotNull(message = "Perfil é obrigatório")
        Perfil perfil

) {
}