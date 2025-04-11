package org.example.exception;

public class EmailInvalidoException extends RuntimeException{
    public EmailInvalidoException() {
        super("Email inválido!!");
    }
}
