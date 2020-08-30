package it.polito.ai.lab2.exceptions;

public class VmIsActiveException extends TeamServiceException {
    public VmIsActiveException(String message){
        super(message);
    }
}
