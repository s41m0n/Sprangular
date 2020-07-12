package it.polito.ai.lab2.exceptions;

public class ProfessorNotFoundException extends TeamServiceException {

    public ProfessorNotFoundException(String message) {
        super(message);
    }
}
