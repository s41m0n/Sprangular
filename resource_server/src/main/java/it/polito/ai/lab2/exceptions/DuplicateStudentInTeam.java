package it.polito.ai.lab2.exceptions;

public class DuplicateStudentInTeam extends RuntimeException{

    public DuplicateStudentInTeam(String message) {
        super(message);
    }
}
