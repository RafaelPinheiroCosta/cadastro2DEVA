package com.senai.cadastro.infrastructure.bootstrap;

import com.senai.cadastro.domain.entity.Perfil;
import com.senai.cadastro.domain.entity.Usuario;
import com.senai.cadastro.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class AdminBootstrap
        implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${bootstrap.admin.enabled}")
    private boolean enabled;

    @Value("${bootstrap.admin.nome}")
    private String nome;

    @Value("${bootstrap.admin.cpf}")
    private String cpf;

    @Value("${bootstrap.admin.email}")
    private String email;

    @Value("${bootstrap.admin.senha}")
    private String senha;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        if (!enabled) {
            log.info("Bootstrap administrativo desabilitado.");
            return;
        }

        if (usuarioRepository.existsByPerfil(Perfil.ADMIN)) {
            log.info("Bootstrap administrativo não executado: já existe um ADMIN.");
            return;
        }

        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);
        String cpfNormalizado = cpf.replaceAll("\\D","");

        Usuario admin = new Usuario(
                        null,
                        passwordEncoder.encode(senha),
                        nome,
                        cpfNormalizado,
                        emailNormalizado,
                        Perfil.ADMIN
        );

        usuarioRepository.save(admin);
        log.info("Administrador bootstrap criado: {}",emailNormalizado);
    }
}
