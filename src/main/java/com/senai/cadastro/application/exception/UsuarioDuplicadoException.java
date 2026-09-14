package com.senai.cadastro.application.exception;

public class UsuarioDuplicadoException
        extends RuntimeException {

    public UsuarioDuplicadoException(
            String message
    ) {

        super(message);
    }
}