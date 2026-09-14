package com.senai.cadastro.application.service;

import com.senai.cadastro.application.dto.LoginRequestDTO;
import com.senai.cadastro.application.dto.LoginResponseDTO;
import com.senai.cadastro.application.exception.CredenciaisInvalidasException;
import com.senai.cadastro.domain.entity.Usuario;
import com.senai.cadastro.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO request) {

        String email = request.email().trim().toLowerCase(Locale.ROOT);

        Usuario usuario = usuarioRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(CredenciaisInvalidasException::new);

        boolean senhaCorreta = passwordEncoder.matches(request.senha(),usuario.getSenha());
        if (!senhaCorreta)
            throw new CredenciaisInvalidasException();

        return jwtService.gerarToken(usuario);
    }

}