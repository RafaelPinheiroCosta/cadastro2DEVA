package com.senai.cadastro.application.service;

import com.senai.cadastro.application.dto.LoginResponseDTO;
import com.senai.cadastro.application.dto.UsuarioResponseDTO;
import com.senai.cadastro.domain.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value(
            "${security.jwt.issuer}"
    )
    private String issuer;

    @Value(
            "${security.jwt.expires-in-seconds}"
    )
    private long expiresInSeconds;

    public LoginResponseDTO gerarToken(
            Usuario usuario
    ) {

        Instant agora =
                Instant.now();

        Instant expiracao =
                agora.plusSeconds(
                        expiresInSeconds
                );

        String authority =
                "ROLE_"
                        + usuario
                            .getPerfil()
                            .name();

        JwtClaimsSet claims =
                JwtClaimsSet
                        .builder()

                        .issuer(
                                issuer
                        )

                        .issuedAt(
                                agora
                        )

                        .expiresAt(
                                expiracao
                        )

                        /*
                         * O subject identifica o usuario
                         * autenticado.
                         */
                        .subject(
                                usuario.getEmail()
                        )

                        .claim(
                                "usuarioId",
                                usuario
                                        .getId()
                                        .toString()
                        )

                        .claim(
                                "perfil",
                                usuario
                                        .getPerfil()
                                        .name()
                        )

                        .claim(
                                "roles",
                                List.of(
                                        authority
                                )
                        )

                        .build();

        JwsHeader header =
                JwsHeader
                        .with(
                                MacAlgorithm.HS256
                        )
                        .type(
                                "JWT"
                        )
                        .build();

        Jwt jwt =
                jwtEncoder.encode(
                        JwtEncoderParameters.from(
                                header,
                                claims
                        )
                );

        return new LoginResponseDTO(
                jwt.getTokenValue(),
                "Bearer",
                expiresInSeconds,
                UsuarioResponseDTO.fromEntity(
                        usuario
                )
        );
    }
}