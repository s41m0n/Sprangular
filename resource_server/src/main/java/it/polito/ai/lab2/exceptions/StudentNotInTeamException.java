package it.polito.ai.lab2.exceptions;

public class StudentNotInTeamException extends TeamServiceException{
    public StudentNotInTeamException(String message){
        super(message);
    }
}
