package com.senai.cadastro.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "usuarios",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_usuario_cpf",columnNames = "cpf"),
                @UniqueConstraint(name = "uk_usuario_email",columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_usuario_nome",columnList = "nome"),
                @Index(name = "idx_usuario_email",columnList = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",nullable = false,updatable = false)
    private UUID id;

    @Column(name = "senha",nullable = false,length = 100)
    private String senha;

    @Column(name = "nome",nullable = false,length = 150)
    private String nome;

    @Column(name = "cpf",nullable = false,length = 11,unique = true)
    private String cpf;

    @Column(name = "email",nullable = false,length = 255,unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil",nullable = false,length = 20)
    private Perfil perfil = Perfil.USER;

    @PrePersist
    @PreUpdate
    private void normalizarDados() {
        if (this.nome != null) {
            this.nome = this.nome.trim();
        }
        if (this.cpf != null) {
            this.cpf = this.cpf.replaceAll("\\D","");
        }
        if (this.email != null) {
            this.email = this.email.trim().toLowerCase();
        }
        if (this.perfil == null) {
            this.perfil = Perfil.USER;
        }
    }
}