package it.polito.ai.lab2.exceptions;

public class StudentAlreadyInTeam extends RuntimeException{

    public StudentAlreadyInTeam(String message) {
        super(message);
    }
}
