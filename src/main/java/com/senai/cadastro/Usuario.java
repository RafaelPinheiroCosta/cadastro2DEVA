package com.senai.cadastro;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.util.UUID;

@Entity
@Table(
        name = "usuarios",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_usuario_cpf", columnNames = "cpf"),
                @UniqueConstraint(name = "uk_usuario_email", columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_usuario_nome", columnList = "nome"),
                @Index(name = "idx_usuario_email", columnList = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 150, message = "Nome deve ter entre 2 e 150 caracteres")
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    //@Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 dígitos")
    @Column(name = "cpf", nullable = false, length = 11, unique = true)
    @CPF(message = "CPF inválido")
    private String cpf;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 255, message = "E-mail deve ter no máximo 255 caracteres")
    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @PrePersist
    @PreUpdate
    private void normalizarDados() {
        if (this.nome != null) {
            this.nome = this.nome.trim();
        }
        if (this.cpf != null) {
            this.cpf = this.cpf.replaceAll("\\D", "");
        }
        if (this.email != null) {
            this.email = this.email.trim().toLowerCase();
        }
    }
}
