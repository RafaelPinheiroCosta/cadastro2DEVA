package com.senai.cadastro.application.dto;

import com.senai.cadastro.domain.entity.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record UsuarioRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 150, message = "Nome deve ter entre 2 e 150 caracteres")
        String nome,
        @CPF(message = "CPF inválido")
        @NotBlank(message = "CPF é obrigatório")
        //@Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 dígitos")
        String cpf,
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Size(max = 255, message = "E-mail deve ter no máximo 255 caracteres")
        String email,

        @NotBlank(message = "Senha é obrigatório")
        @Size(min = 4, max = 8, message = "Senha deve ter entre 4 e 8 caracteres")
        String senha
) {
    public Usuario toEntity() {
        return new Usuario(
                null,
                senha,
                nome,
                cpf,
                email
        );
    }
}
