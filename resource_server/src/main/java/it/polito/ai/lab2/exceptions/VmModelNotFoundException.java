package it.polito.ai.lab2.exceptions;

public class VmModelNotFoundException extends TeamServiceException{
    public VmModelNotFoundException(String message){
        super(message);
    }
}
