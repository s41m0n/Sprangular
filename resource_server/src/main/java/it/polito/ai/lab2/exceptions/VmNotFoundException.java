package it.polito.ai.lab2.exceptions;

public class VmNotFoundException extends TeamServiceException {
    public VmNotFoundException(String message) {
        super(message);
    }
}
