package it.polito.ai.lab2.exceptions;

public class StudentAlreadyInTeam extends TeamServiceException {
    public StudentAlreadyInTeam(String message) {
        super(message);
    }
}
