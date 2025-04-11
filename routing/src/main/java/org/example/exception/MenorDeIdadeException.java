package org.example.exception;

public class MenorDeIdadeException extends RuntimeException{
    public MenorDeIdadeException(){
        super("Não é possível cadastrar usuário menor de idade!!");
    }
}
