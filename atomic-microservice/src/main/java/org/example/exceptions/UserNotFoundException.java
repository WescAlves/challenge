package org.example.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException() {
        super("Could not find the user you're looking for, plese make sure the given id exists.");
    }
}
