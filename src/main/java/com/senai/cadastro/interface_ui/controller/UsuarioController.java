package com.senai.cadastro.interface_ui.controller;

import com.senai.cadastro.application.dto.PerfilUpdateDTO;
import com.senai.cadastro.application.dto.UsuarioRequestDTO;
import com.senai.cadastro.application.dto.UsuarioResponseDTO;
import com.senai.cadastro.application.service.UsuarioService;
import com.senai.cadastro.infrastructure.config.OpenApiExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor

@SecurityRequirement(
        name = "bearerAuth"
)

@Tag(
        name = "Administração de usuários",
        description =
                "Operações administrativas. Todos os endpoints exigem perfil ADMIN."
)
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping

    @Operation(
            summary = "Listar usuários",
            description =
                    "Disponível exclusivamente para ADMIN."
    )

    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Usuários encontrados",

                    content = @Content(

                            mediaType = "application/json",

                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.USERS
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado",

                    content = @Content(

                            mediaType =
                                    "application/problem+json",

                            schema = @Schema(
                                    implementation =
                                            ProblemDetail.class
                            ),

                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.UNAUTHORIZED
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "403",
                    description =
                            "Usuário autenticado sem perfil ADMIN",

                    content = @Content(

                            mediaType =
                                    "application/problem+json",

                            schema = @Schema(
                                    implementation =
                                            ProblemDetail.class
                            ),

                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.FORBIDDEN
                            )
                    )
            )
    })

    public ResponseEntity<List<UsuarioResponseDTO>>
    listarTodosUsuarios() {

        return ResponseEntity.ok(
                usuarioService.findAll()
        );
    }

    @GetMapping("/{id}")

    @Operation(
            summary = "Buscar usuário por ID"
    )

    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário encontrado",

                    content = @Content(

                            mediaType = "application/json",

                            schema = @Schema(
                                    implementation =
                                            UsuarioResponseDTO.class
                            ),

                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.USER
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "UUID inválido",

                    content = @Content(

                            mediaType =
                                    "application/problem+json",

                            schema = @Schema(
                                    implementation =
                                            ProblemDetail.class
                            ),

                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples
                                                    .INVALID_PARAMETER
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado",

                    content = @Content(
                            mediaType =
                                    "application/problem+json",
                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.UNAUTHORIZED
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado",

                    content = @Content(
                            mediaType =
                                    "application/problem+json",
                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.FORBIDDEN
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description =
                            "Usuário não encontrado",

                    content = @Content(

                            mediaType =
                                    "application/problem+json",

                            schema = @Schema(
                                    implementation =
                                            ProblemDetail.class
                            ),

                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.NOT_FOUND
                            )
                    )
            )
    })

    public ResponseEntity<UsuarioResponseDTO>
    buscarUsuarioPorId(
            @PathVariable
            UUID id
    ) {

        return ResponseEntity.ok(
                usuarioService.findById(
                        id
                )
        );
    }

    @PostMapping

    @Operation(
            summary =
                    "Cadastrar usuário administrativamente",
            description = """
                    Cria um novo USER.

                    A criação de um ADMIN ocorre em duas etapas:

                    1. cadastrar o usuário;
                    2. alterar o perfil pelo endpoint PATCH /usuario/{id}/perfil.
                    """
    )

    @ApiResponses({

            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário criado",

                    content = @Content(

                            mediaType =
                                    "application/json",

                            schema = @Schema(
                                    implementation =
                                            UsuarioResponseDTO.class
                            ),

                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.USER
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos",

                    content = @Content(

                            mediaType =
                                    "application/problem+json",

                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples
                                                    .VALIDATION_ERROR
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado",

                    content = @Content(
                            mediaType =
                                    "application/problem+json",
                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.UNAUTHORIZED
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado",

                    content = @Content(
                            mediaType =
                                    "application/problem+json",
                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.FORBIDDEN
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "409",
                    description =
                            "CPF ou e-mail duplicado",

                    content = @Content(

                            mediaType =
                                    "application/problem+json",

                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.CONFLICT
                            )
                    )
            )
    })

    public ResponseEntity<UsuarioResponseDTO>
    cadastrarUsuario(
            @Valid
            @RequestBody
            UsuarioRequestDTO usuarioRequestDTO
    ) {

        UsuarioResponseDTO usuarioSalvo =
                usuarioService.save(
                        usuarioRequestDTO
                );

        return ResponseEntity
                .created(
                        URI.create(
                                "/usuario/"
                                        + usuarioSalvo.id()
                        )
                )
                .body(
                        usuarioSalvo
                );
    }

    @PutMapping("/{id}")

    @Operation(
            summary = "Atualizar usuário"
    )

    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário atualizado",

                    content = @Content(

                            mediaType = "application/json",

                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.USER
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description =
                            "UUID ou dados inválidos",

                    content = @Content(
                            mediaType =
                                    "application/problem+json"
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado",

                    content = @Content(
                            mediaType =
                                    "application/problem+json",
                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.UNAUTHORIZED
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado",

                    content = @Content(
                            mediaType =
                                    "application/problem+json",
                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.FORBIDDEN
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description =
                            "Usuário não encontrado",

                    content = @Content(
                            mediaType =
                                    "application/problem+json",
                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.NOT_FOUND
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "409",
                    description =
                            "CPF ou e-mail duplicado",

                    content = @Content(
                            mediaType =
                                    "application/problem+json",
                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.CONFLICT
                            )
                    )
            )
    })

    public ResponseEntity<UsuarioResponseDTO>
    atualizarUsuario(
            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UsuarioRequestDTO usuarioRequestDTO
    ) {

        return ResponseEntity.ok(
                usuarioService.update(
                        id,
                        usuarioRequestDTO
                )
        );
    }

    @PatchMapping("/{id}/perfil")

    @Operation(
            summary = "Alterar perfil",
            description = """
                    Endpoint exclusivo de ADMIN.

                    Permite promover:

                    USER -> ADMIN

                    ou rebaixar:

                    ADMIN -> USER
                    """
    )

    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Perfil alterado",

                    content = @Content(

                            mediaType = "application/json",

                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.ADMIN
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description =
                            "UUID ou perfil inválido",

                    content = @Content(
                            mediaType =
                                    "application/problem+json"
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado",

                    content = @Content(
                            mediaType =
                                    "application/problem+json",
                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.UNAUTHORIZED
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado",

                    content = @Content(
                            mediaType =
                                    "application/problem+json",
                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.FORBIDDEN
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description =
                            "Usuário não encontrado",

                    content = @Content(
                            mediaType =
                                    "application/problem+json",
                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.NOT_FOUND
                            )
                    )
            )
    })

    public ResponseEntity<UsuarioResponseDTO>
    alterarPerfil(
            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            PerfilUpdateDTO perfilUpdateDTO
    ) {

        return ResponseEntity.ok(
                usuarioService.updatePerfil(
                        id,
                        perfilUpdateDTO
                )
        );
    }

    @DeleteMapping("/{id}")

    @Operation(
            summary = "Excluir usuário"
    )

    @ApiResponses({

            @ApiResponse(
                    responseCode = "204",
                    description =
                            "Usuário excluído com sucesso"
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "UUID inválido",

                    content = @Content(
                            mediaType =
                                    "application/problem+json"
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado",

                    content = @Content(
                            mediaType =
                                    "application/problem+json",
                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.UNAUTHORIZED
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado",

                    content = @Content(
                            mediaType =
                                    "application/problem+json",
                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.FORBIDDEN
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description =
                            "Usuário não encontrado",

                    content = @Content(
                            mediaType =
                                    "application/problem+json",
                            examples = @ExampleObject(
                                    value =
                                            OpenApiExamples.NOT_FOUND
                            )
                    )
            )
    })

    public ResponseEntity<Void>
    deletarUsuario(
            @PathVariable
            UUID id
    ) {

        usuarioService.delete(
                id
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}