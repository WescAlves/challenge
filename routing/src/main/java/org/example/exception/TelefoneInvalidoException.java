package org.example.exception;

public class TelefoneInvalidoException extends RuntimeException{
    public TelefoneInvalidoException(){
        super("Telefone inválido!!");
    }
}
