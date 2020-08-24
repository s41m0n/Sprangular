package it.polito.ai.lab2.exceptions;

public class TeamNotFoundException extends TeamServiceException{
    public TeamNotFoundException(String message) {
        super(message);
    }
}
