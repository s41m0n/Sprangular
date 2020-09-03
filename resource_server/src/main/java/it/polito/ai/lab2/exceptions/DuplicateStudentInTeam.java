package it.polito.ai.lab2.exceptions;

public class DuplicateStudentInTeam extends TeamServiceException {
    public DuplicateStudentInTeam(String message) {
        super(message);
    }
}
