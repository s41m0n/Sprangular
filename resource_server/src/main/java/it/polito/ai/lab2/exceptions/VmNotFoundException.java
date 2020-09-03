package it.polito.ai.lab2.exceptions;

public class VmNotFoundException extends VmServiceException {
    public VmNotFoundException(String message) {
        super(message);
    }
}
