package com.senai.cadastro.application.exception;

public class CredenciaisInvalidasException
        extends RuntimeException {

    public CredenciaisInvalidasException() {
        super("E-mail ou senha inválidos");
    }
}