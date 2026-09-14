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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void openApiDeveSerPublica() throws Exception {
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
    }

    @Test
    void endpointsProtegidosSemTokenDevemRetornar401() throws Exception {
        mockMvc.perform(get("/usuario")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/usuario/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void adminBootstrapDeveAutenticarEAcessarCrudEMe() throws Exception {
        LoginData admin = login("admin@cadastro.local","Adm@1234");

        mockMvc.perform(get("/usuario")
                        .header("Authorization","Bearer " + admin.token()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/usuario/me")
                        .header("Authorization","Bearer " + admin.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@cadastro.local"))
                .andExpect(jsonPath("$.perfil").value("ADMIN"));
    }

    @Test
    void userDeveAcessarMeMasNaoPodeAcessarCrudAdmin() throws Exception {
        LoginData admin = login("admin@cadastro.local","Adm@1234");

        String body = """
                {
                  "nome": "Usuario Teste",
                  "cpf": "11144477735",
                  "email": "usuario.teste@email.com",
                  "senha": "Usr@1234"
                }
                """;

        mockMvc.perform(post("/usuario")
                        .header("Authorization","Bearer " + admin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.perfil").value("USER"));

        LoginData user = login("usuario.teste@email.com","Usr@1234");

        mockMvc.perform(get("/usuario/me")
                        .header("Authorization","Bearer " + user.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("usuario.teste@email.com"))
                .andExpect(jsonPath("$.perfil").value("USER"))
                .andExpect(jsonPath("$.senha").doesNotExist());

        mockMvc.perform(get("/usuario")
                        .header("Authorization","Bearer " + user.token()))
                .andExpect(status().isForbidden());
    }

    @Test
    void ultimoAdministradorNaoPodeSerRebaixadoNemExcluido() throws Exception {
        LoginData admin = login("admin@cadastro.local","Adm@1234");

        mockMvc.perform(patch("/usuario/{id}/perfil",admin.usuarioId())
                        .header("Authorization","Bearer " + admin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "perfil": "USER"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Operação administrativa inválida"));

        mockMvc.perform(delete("/usuario/{id}",admin.usuarioId())
                        .header("Authorization","Bearer " + admin.token()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Operação administrativa inválida"));
    }

    @Test
    void loginComSenhaErradaDeveRetornar401() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@cadastro.local",
                                  "senha": "errada"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    private LoginData login(String email,String senha) throws Exception {
        String body = """
                {
                  "email": "%s",
                  "senha": "%s"
                }
                """.formatted(email,senha);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return new LoginData(extract(response,"token"),extract(response,"id"));
    }

    private String extract(String json,String field) {
        Matcher matcher = Pattern.compile("\"" + field + "\"\\s*:\\s*\"([^\"]+)\"").matcher(json);

        if (!matcher.find())
            throw new IllegalStateException("Campo '" + field + "' não encontrado na resposta: " + json);

        return matcher.group(1);
    }

    private record LoginData(String token,String usuarioId) {
    }
}
