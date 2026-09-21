package com.senai.cadastro.interface_ui.controller;

import com.senai.cadastro.application.dto.LoginRequestDTO;
import com.senai.cadastro.application.dto.LoginResponseDTO;
import com.senai.cadastro.application.service.AuthService;
import com.senai.cadastro.infrastructure.config.OpenApiExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

@Tag(
        name = "Autenticação",
        description = "Autenticação e emissão de token JWT"
)
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Realizar login",
            description = """
            Valida e-mail e senha.
            
            Em caso de sucesso é emitido um JWT assinado
            contendo a identidade e o perfil do usuário.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class),
                            examples = @ExampleObject(value = OpenApiExamples.LOGIN)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados da requisição inválidos",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "E-mail ou senha incorretos",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(value = OpenApiExamples.INVALID_CREDENTIALS)
                    )
            )
    })
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

}
