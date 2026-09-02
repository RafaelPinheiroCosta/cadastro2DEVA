package com.senai.cadastro.domain.repository;

import com.senai.cadastro.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Usuario findByEmail(String email);

    Boolean existsByCpf(String cpf);
}
