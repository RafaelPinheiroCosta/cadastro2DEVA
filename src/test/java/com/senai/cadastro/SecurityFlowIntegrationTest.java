package com.senai.cadastro;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void openApiDeveSerPublica()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/v3/api-docs"
                        )
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    void usuarioAdministrativoSemTokenDeveRetornar401()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/usuario"
                        )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void adminBootstrapDeveAutenticarEAcessarCrud()
            throws Exception {

        String token =
                login(
                        "admin@cadastro.local",
                        "Adm@1234"
                );

        mockMvc.perform(
                        get(
                                "/usuario"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    void userDeveAutenticarMasNaoPodeAcessarCrudAdmin()
            throws Exception {

        String body = """
                {
                  "nome": "Usuario Teste",
                  "cpf": "11144477735",
                  "email": "usuario.teste@email.com",
                  "senha": "Usr@1234"
                }
                """;

        mockMvc.perform(
                        post(
                                "/auth/register"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        body
                                )
                )

                .andExpect(
                        status().isCreated()
                )

                .andExpect(
                        jsonPath(
                                "$.perfil"
                        )
                                .value(
                                        "USER"
                                )
                );

        String token =
                login(
                        "usuario.teste@email.com",
                        "Usr@1234"
                );

        /*
         * USER autenticado pode consultar sua identidade.
         */
        mockMvc.perform(
                        get(
                                "/auth/me"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )

                .andExpect(
                        status().isOk()
                )

                .andExpect(
                        jsonPath(
                                "$.email"
                        )
                                .value(
                                        "usuario.teste@email.com"
                                )
                )

                .andExpect(
                        jsonPath(
                                "$.perfil"
                        )
                                .value(
                                        "USER"
                                )
                );

        /*
         * Mas USER nao pode acessar area ADMIN.
         */
        mockMvc.perform(
                        get(
                                "/usuario"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void loginComSenhaErradaDeveRetornar401()
            throws Exception {

        String body = """
                {
                  "email": "admin@cadastro.local",
                  "senha": "errada"
                }
                """;

        mockMvc.perform(
                        post(
                                "/auth/login"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        body
                                )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    private String login(
            String email,
            String senha
    ) throws Exception {

        String body = """
                {
                  "email": "%s",
                  "senha": "%s"
                }
                """.formatted(
                email,
                senha
        );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/auth/login"
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                body
                                        )
                        )

                        .andExpect(
                                status().isOk()
                        )

                        .andExpect(
                                jsonPath(
                                        "$.tipo"
                                )
                                        .value(
                                                "Bearer"
                                        )
                        )

                        .andReturn();

        String response =
                result
                        .getResponse()
                        .getContentAsString();

        Pattern pattern =
                Pattern.compile(
                        "\"token\"\\s*:\\s*\"([^\"]+)\""
                );

        Matcher matcher =
                pattern.matcher(
                        response
                );

        if (!matcher.find()) {

            throw new IllegalStateException(
                    "Token JWT não encontrado na resposta: "
                            + response
            );
        }

        return matcher.group(
                1
        );
    }
}