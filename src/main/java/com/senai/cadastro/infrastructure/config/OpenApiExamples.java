package com.senai.cadastro.infrastructure.config;

public final class OpenApiExamples {

    private OpenApiExamples() {
    }

    public static final String USER = """
            {
              "id": "550e8400-e29b-41d4-a716-446655440000",
              "nome": "João da Silva",
              "cpf": "11144477735",
              "email": "usuario@email.com",
              "perfil": "USER"
            }
            """;

    public static final String ADMIN = """
            {
              "id": "550e8400-e29b-41d4-a716-446655440001",
              "nome": "Administrador",
              "cpf": "52998224725",
              "email": "admin@cadastro.local",
              "perfil": "ADMIN"
            }
            """;

    public static final String USERS = """
            [
              {
                "id": "550e8400-e29b-41d4-a716-446655440001",
                "nome": "Administrador",
                "cpf": "52998224725",
                "email": "admin@cadastro.local",
                "perfil": "ADMIN"
              },
              {
                "id": "550e8400-e29b-41d4-a716-446655440000",
                "nome": "João da Silva",
                "cpf": "11144477735",
                "email": "usuario@email.com",
                "perfil": "USER"
              }
            ]
            """;

    public static final String LOGIN = """
            {
              "token": "eyJhbGciOiJIUzI1NiJ9...",
              "tipo": "Bearer",
              "expiresIn": 3600,
              "usuario": {
                "id": "550e8400-e29b-41d4-a716-446655440001",
                "nome": "Administrador",
                "cpf": "52998224725",
                "email": "admin@cadastro.local",
                "perfil": "ADMIN"
              }
            }
            """;

    public static final String VALIDATION_ERROR = """
            {
              "type": "about:blank",
              "title": "Erro de validação",
              "status": 400,
              "detail": "Um ou mais campos são inválidos",
              "instance": "/usuario",
              "timestamp": "2026-09-14T12:00:00",
              "application": "cadastroAPI",
              "errors": {
                "email": "E-mail inválido",
                "senha": "Senha deve ter entre 4 e 8 caracteres"
              }
            }
            """;

    public static final String INVALID_PARAMETER = """
            {
              "type": "about:blank",
              "title": "Tipo de parâmetro inválido",
              "status": 400,
              "detail": "O parâmetro 'id' deve ser do tipo 'UUID'.",
              "instance": "/usuario/abc",
              "timestamp": "2026-09-14T12:00:00",
              "application": "cadastroAPI"
            }
            """;

    public static final String UNAUTHORIZED = """
            {
              "type": "about:blank",
              "title": "Não autenticado",
              "status": 401,
              "detail": "Autenticação necessária ou token ausente, inválido ou expirado",
              "instance": "/usuario",
              "timestamp": "2026-09-14T12:00:00",
              "application": "cadastroAPI"
            }
            """;

    public static final String INVALID_CREDENTIALS = """
            {
              "type": "about:blank",
              "title": "Credenciais inválidas",
              "status": 401,
              "detail": "E-mail ou senha inválidos",
              "instance": "/auth/login",
              "timestamp": "2026-09-14T12:00:00",
              "application": "cadastroAPI"
            }
            """;

    public static final String FORBIDDEN = """
            {
              "type": "about:blank",
              "title": "Acesso negado",
              "status": 403,
              "detail": "O usuário está autenticado, mas não possui permissão para acessar este recurso",
              "instance": "/usuario",
              "timestamp": "2026-09-14T12:00:00",
              "application": "cadastroAPI"
            }
            """;

    public static final String NOT_FOUND = """
            {
              "type": "about:blank",
              "title": "Usuário não encontrado",
              "status": 404,
              "detail": "Usuário não encontrado",
              "instance": "/usuario/550e8400-e29b-41d4-a716-446655440099",
              "timestamp": "2026-09-14T12:00:00",
              "application": "cadastroAPI"
            }
            """;

    public static final String CONFLICT = """
            {
              "type": "about:blank",
              "title": "Usuário duplicado",
              "status": 409,
              "detail": "Já existe um usuário cadastrado com este e-mail",
              "instance": "/usuario",
              "timestamp": "2026-09-14T12:00:00",
              "application": "cadastroAPI"
            }
            """;
}
