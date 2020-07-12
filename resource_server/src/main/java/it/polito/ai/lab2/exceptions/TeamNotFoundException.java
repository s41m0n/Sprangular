package it.polito.ai.lab2.exceptions;

public class TeamNotFoundException extends RuntimeException{

    public TeamNotFoundException(String message) {
        super(message);
    }
}
