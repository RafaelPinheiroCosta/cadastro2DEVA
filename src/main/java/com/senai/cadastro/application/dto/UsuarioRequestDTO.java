package com.senai.cadastro.application.dto;

import com.senai.cadastro.domain.entity.Perfil;
import com.senai.cadastro.domain.entity.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

@Schema(
        name = "UsuarioRequest",
        description = "Dados utilizados para cadastrar ou atualizar um usuário"
)
public record UsuarioRequestDTO(

        @Schema(
                description = "Nome do usuário",
                example = "João da Silva"
        )
        @NotBlank(
                message = "Nome é obrigatório"
        )
        @Size(
                min = 2,
                max = 150,
                message =
                        "Nome deve ter entre 2 e 150 caracteres"
        )
        String nome,

        @Schema(
                description = "CPF válido",
                example = "11144477735"
        )
        @CPF(
                message = "CPF inválido"
        )
        @NotBlank(
                message = "CPF é obrigatório"
        )
        String cpf,

        @Schema(
                description = "E-mail do usuário",
                example = "usuario@email.com"
        )
        @NotBlank(
                message = "E-mail é obrigatório"
        )
        @Email(
                message = "E-mail inválido"
        )
        @Size(
                max = 255,
                message =
                        "E-mail deve ter no máximo 255 caracteres"
        )
        String email,

        @Schema(
                description =
                        "Senha original. Ela será convertida para BCrypt antes da persistência.",
                example = "Ab@123",
                minLength = 4,
                maxLength = 8
        )
        @NotBlank(
                message = "Senha é obrigatório"
        )
        @Size(
                min = 4,
                max = 8,
                message =
                        "Senha deve ter entre 4 e 8 caracteres"
        )
        String senha

) {

    public Usuario toEntity() {

        return new Usuario(
                null,
                senha,
                nome,
                cpf,
                email,
                Perfil.USER
        );
    }
}