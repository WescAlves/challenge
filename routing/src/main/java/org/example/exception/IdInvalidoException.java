package org.example.exception;

public class IdInvalidoException extends RuntimeException{
    public IdInvalidoException(){
        super("Por favor, insira um Id válido para a consulta!!");
    }
}
