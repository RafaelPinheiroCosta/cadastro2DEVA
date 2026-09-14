package com.senai.cadastro.interface_ui.exception;

import com.senai.cadastro.application.exception.CredenciaisInvalidasException;
import com.senai.cadastro.application.exception.UsuarioDuplicadoException;
import com.senai.cadastro.application.exception.UsuarioNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

import static com.senai.cadastro.interface_ui.exception.ProblemDetailUtils.buildProblem;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            MethodArgumentTypeMismatchException.class
    )
    public ProblemDetail handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {

        return buildProblem(

                HttpStatus.BAD_REQUEST,

                "Tipo de parâmetro inválido",

                String.format(
                        "O parâmetro '%s' deve ser do tipo '%s'. Valor recebido: '%s'",
                        ex.getName(),
                        ex.getRequiredType() != null
                                ? ex.getRequiredType().getSimpleName()
                                : "desconhecido",
                        ex.getValue()
                ),

                request.getRequestURI()
        );
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ProblemDetail badRequest(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        ProblemDetail problem =
                buildProblem(

                        HttpStatus.BAD_REQUEST,

                        "Erro de validação",

                        "Um ou mais campos são inválidos",

                        request.getRequestURI()
                );

        Map<String, String> errors =
                new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(
                        error ->
                                errors.put(
                                        error.getField(),
                                        error.getDefaultMessage()
                                )
                );

        problem.setProperty(
                "errors",
                errors
        );

        return problem;
    }

    @ExceptionHandler(
            HttpMessageNotReadableException.class
    )
    public ProblemDetail
    handleInvalidBody(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {

        return buildProblem(

                HttpStatus.BAD_REQUEST,

                "Corpo da requisição inválido",

                "O JSON enviado não pôde ser interpretado",

                request.getRequestURI()
        );
    }

    @ExceptionHandler(
            UsuarioNaoEncontradoException.class
    )
    public ProblemDetail
    handleUsuarioNaoEncontrado(
            UsuarioNaoEncontradoException ex,
            HttpServletRequest request
    ) {

        return buildProblem(

                HttpStatus.NOT_FOUND,

                "Usuário não encontrado",

                ex.getMessage(),

                request.getRequestURI()
        );
    }

    @ExceptionHandler(
            UsuarioDuplicadoException.class
    )
    public ProblemDetail
    handleUsuarioDuplicado(
            UsuarioDuplicadoException ex,
            HttpServletRequest request
    ) {

        return buildProblem(

                HttpStatus.CONFLICT,

                "Usuário duplicado",

                ex.getMessage(),

                request.getRequestURI()
        );
    }

    /*
     * Segunda barreira contra conflitos de unicidade.
     *
     * Mesmo com validacao preventiva no service,
     * o banco continua sendo a fonte final da verdade.
     */
    @ExceptionHandler(
            DataIntegrityViolationException.class
    )
    public ProblemDetail
    handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {

        return buildProblem(

                HttpStatus.CONFLICT,

                "Conflito de dados",

                "A operação viola uma restrição de integridade dos dados",

                request.getRequestURI()
        );
    }

    @ExceptionHandler(
            CredenciaisInvalidasException.class
    )
    public ProblemDetail
    handleCredenciaisInvalidas(
            CredenciaisInvalidasException ex,
            HttpServletRequest request
    ) {

        return buildProblem(

                HttpStatus.UNAUTHORIZED,

                "Credenciais inválidas",

                ex.getMessage(),

                request.getRequestURI()
        );
    }

    /*
     * Mantido do codigo atual.
     */
    @ExceptionHandler(
            InternalError.class
    )
    public ProblemDetail handleInternalError(
            InternalError ex,
            HttpServletRequest request
    ) {

        return buildProblem(

                HttpStatus.INTERNAL_SERVER_ERROR,

                "Erro interno do servidor",

                "Ocorreu um erro interno no servidor.",

                request.getRequestURI()
        );
    }
}