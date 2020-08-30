package it.polito.ai.lab2.exceptions;

public class TooManyActualResourcesException extends TeamServiceException{
    public TooManyActualResourcesException(String message){
        super(message);
    }
}
