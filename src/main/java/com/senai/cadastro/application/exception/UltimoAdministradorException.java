package com.senai.cadastro.application.exception;

public class UltimoAdministradorException extends RuntimeException {

    public UltimoAdministradorException() {
        super("O último administrador do sistema não pode ser removido ou rebaixado.");
    }
}
