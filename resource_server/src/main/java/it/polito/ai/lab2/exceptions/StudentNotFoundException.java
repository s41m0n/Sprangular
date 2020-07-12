package it.polito.ai.lab2.exceptions;

public class StudentNotFoundException extends TeamServiceException {

    public StudentNotFoundException(String message) {
        super(message);
    }
}
