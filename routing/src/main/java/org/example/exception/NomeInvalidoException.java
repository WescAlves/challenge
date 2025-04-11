package org.example.exception;

public class NomeInvalidoException extends RuntimeException{
    public NomeInvalidoException() {
        super("Nome inválido!!");
    }
}
