package com.senai.cadastro.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Cadastro API",
                version = "1.0.0",
                description = """
                        API didática de cadastro de usuários com:
                        
                        - autenticação;
                        - autorização;
                        - JWT;
                        - BCrypt;
                        - perfis USER e ADMIN;
                        - tratamento de erros com ProblemDetail;
                        - documentação OpenAPI.
                        
                        Fluxo:
                        
                        1. POST /auth/register cria um USER;
                        2. POST /auth/login autentica o usuário;
                        3. o servidor devolve um JWT;
                        4. o cliente envia Authorization: Bearer <token>;
                        5. GET /auth/me exige autenticação;
                        6. /usuario/** exige perfil ADMIN;
                        7. um ADMIN pode promover outro usuário para ADMIN.
                        """,
                license = @License(name = "Uso educacional")
        ),
        servers = {
                @Server(url = "http://localhost:8080",description = "Ambiente local")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Informe somente o token JWT retornado por POST /auth/login"
)
public class OpenApiConfig {
}