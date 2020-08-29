package it.polito.ai.lab2.exceptions;

public class CannotDeleteVmException extends TeamServiceException {
    public CannotDeleteVmException(String message){
        super(message);
    }
}
