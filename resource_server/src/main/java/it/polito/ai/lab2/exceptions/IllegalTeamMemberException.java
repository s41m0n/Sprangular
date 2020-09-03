package it.polito.ai.lab2.exceptions;

public class IllegalTeamMemberException extends TeamServiceException {
    public IllegalTeamMemberException(String message) {
        super(message);
    }
}
