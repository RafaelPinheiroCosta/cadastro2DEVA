package com.senai.cadastro.domain.repository;

import com.senai.cadastro.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByCpf(String cpf);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);

    boolean existsByCpfAndIdNot(String cpf,UUID id);
}