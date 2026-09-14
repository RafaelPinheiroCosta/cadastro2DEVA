package com.senai.cadastro.infrastructure.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {

        /*
         * BCrypt possui salt automatico.
         *
         * A senha original nao pode ser recuperada
         * a partir do hash.
         */
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecretKey jwtSecretKey(
            @Value("${security.jwt.secret}")
            String encodedSecret
    ) {

        byte[] keyBytes;

        try {

            keyBytes =
                    Base64
                            .getDecoder()
                            .decode(
                                    encodedSecret
                            );

        }
        catch (IllegalArgumentException exception) {

            throw new IllegalStateException(
                    "JWT_SECRET deve estar codificado em Base64",
                    exception
            );
        }

        /*
         * HS256 exige uma chave segura.
         *
         * 32 bytes = 256 bits.
         */
        if (keyBytes.length < 32) {

            throw new IllegalStateException(
                    "JWT_SECRET deve representar no mínimo 256 bits (32 bytes)"
            );
        }

        return new SecretKeySpec(
                keyBytes,
                "HmacSHA256"
        );
    }

    @Bean
    public JwtEncoder jwtEncoder(
            SecretKey jwtSecretKey
    ) {

        return NimbusJwtEncoder
                .withSecretKey(
                        jwtSecretKey
                )
                .algorithm(
                        MacAlgorithm.HS256
                )
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(
            SecretKey jwtSecretKey,

            @Value("${security.jwt.issuer}")
            String issuer
    ) {

        NimbusJwtDecoder decoder =
                NimbusJwtDecoder
                        .withSecretKey(
                                jwtSecretKey
                        )
                        .macAlgorithm(
                                MacAlgorithm.HS256
                        )
                        .build();

        /*
         * Alem da assinatura e expiracao,
         * tambem validamos o emissor.
         */
        decoder.setJwtValidator(
                JwtValidators
                        .createDefaultWithIssuer(
                                issuer
                        )
        );

        return decoder;
    }

    @Bean
    public JwtAuthenticationConverter
    jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter
                authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        /*
         * O JwtService grava:
         *
         * roles = ["ROLE_USER"]
         *
         * ou:
         *
         * roles = ["ROLE_ADMIN"]
         */
        authoritiesConverter
                .setAuthoritiesClaimName(
                        "roles"
                );

        /*
         * O prefixo ROLE_ ja esta dentro do token.
         */
        authoritiesConverter
                .setAuthorityPrefix(
                        ""
                );

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter
                .setJwtGrantedAuthoritiesConverter(
                        authoritiesConverter
                );

        return converter;
    }

    @Bean
    public AuthenticationEntryPoint
    restAuthenticationEntryPoint() {

        return (
                request,
                response,
                exception
        ) -> writeProblem(
                response,
                HttpStatus.UNAUTHORIZED,
                "Não autenticado",
                "Autenticação necessária ou token ausente, inválido ou expirado",
                request.getRequestURI()
        );
    }

    @Bean
    public AccessDeniedHandler
    restAccessDeniedHandler() {

        return (
                request,
                response,
                exception
        ) -> writeProblem(
                response,
                HttpStatus.FORBIDDEN,
                "Acesso negado",
                "O usuário está autenticado, mas não possui permissão para acessar este recurso",
                request.getRequestURI()
        );
    }

    @Bean
    public SecurityFilterChain
    securityFilterChain(
            HttpSecurity http,

            JwtAuthenticationConverter
                    jwtAuthenticationConverter,

            AuthenticationEntryPoint
                    authenticationEntryPoint,

            AccessDeniedHandler
                    accessDeniedHandler
    ) throws Exception {

        http

                /*
                 * Esta API nao utiliza sessao HTTP
                 * como mecanismo de autenticacao.
                 *
                 * O cliente envia o JWT em cada request.
                 */
                .sessionManagement(
                        session ->
                                session
                                        .sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS
                                        )
                )

                /*
                 * Para esta API REST stateless
                 * baseada em Authorization Bearer.
                 */
                .csrf(
                        csrf ->
                                csrf.disable()
                )

                .authorizeHttpRequests(
                        authorize ->
                                authorize

                                        /*
                                         * Cadastro e login
                                         * nao exigem token.
                                         */
                                        .requestMatchers(
                                                "/auth/register",
                                                "/auth/login"
                                        )
                                        .permitAll()

                                        /*
                                         * Swagger/OpenAPI publico.
                                         */
                                        .requestMatchers(
                                                "/v3/api-docs/**",
                                                "/swagger-ui/**",
                                                "/swagger-ui.html"
                                        )
                                        .permitAll()

                                        /*
                                         * Console H2 apenas para
                                         * ambiente local/didatico.
                                         */
                                        .requestMatchers(
                                                "/h2-console/**"
                                        )
                                        .permitAll()

                                        /*
                                         * Toda administracao de usuarios
                                         * exige perfil ADMIN.
                                         */
                                        .requestMatchers(
                                                "/usuario/**"
                                        )
                                        .hasRole(
                                                "ADMIN"
                                        )

                                        /*
                                         * Demais recursos:
                                         * basta estar autenticado.
                                         *
                                         * Exemplo:
                                         * GET /auth/me
                                         */
                                        .anyRequest()
                                        .authenticated()
                )

                /*
                 * Necessario para permitir que o H2 Console
                 * seja exibido dentro do frame da pagina.
                 */
                .headers(
                        headers ->
                                headers
                                        .frameOptions(
                                                frame ->
                                                        frame.sameOrigin()
                                        )
                )

                .exceptionHandling(
                        exceptions ->
                                exceptions
                                        .authenticationEntryPoint(
                                                authenticationEntryPoint
                                        )
                                        .accessDeniedHandler(
                                                accessDeniedHandler
                                        )
                )

                /*
                 * Habilita o processamento:
                 *
                 * Authorization: Bearer <jwt>
                 */
                .oauth2ResourceServer(
                        oauth2 ->
                                oauth2

                                        .jwt(
                                                jwt ->
                                                        jwt
                                                                .jwtAuthenticationConverter(
                                                                        jwtAuthenticationConverter
                                                                )
                                        )

                                        .authenticationEntryPoint(
                                                authenticationEntryPoint
                                        )

                                        .accessDeniedHandler(
                                                accessDeniedHandler
                                        )
                );

        return http.build();
    }

    /*
     * Spring Security intercepta 401 e 403
     * antes de chegar ao ControllerAdvice.
     *
     * Por isso escrevemos aqui uma resposta
     * no mesmo formato RFC 9457 / ProblemDetail
     * usado pelo restante da aplicacao.
     */
    private static void writeProblem(
            HttpServletResponse response,
            HttpStatus status,
            String title,
            String detail,
            String path
    ) throws IOException {

        response.setStatus(
                status.value()
        );

        response.setContentType(
                "application/problem+json"
        );

        response.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        String json =
                "{"
                        + "\"type\":\"about:blank\","
                        + "\"title\":\""
                        + escape(title)
                        + "\","
                        + "\"status\":"
                        + status.value()
                        + ","
                        + "\"detail\":\""
                        + escape(detail)
                        + "\","
                        + "\"instance\":\""
                        + escape(path)
                        + "\","
                        + "\"timestamp\":\""
                        + LocalDateTime.now()
                        + "\","
                        + "\"application\":\"cadastroAPI\""
                        + "}";

        response
                .getWriter()
                .write(
                        json
                );
    }

    private static String escape(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                );
    }
}