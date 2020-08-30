package it.polito.ai.lab2.exceptions;

public class TooManyVmInstancesException extends TeamServiceException {
    public TooManyVmInstancesException(String message){
        super(message);
    }
}
