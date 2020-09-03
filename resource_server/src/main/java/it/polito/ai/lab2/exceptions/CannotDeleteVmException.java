package it.polito.ai.lab2.exceptions;

public class CannotDeleteVmException extends VmServiceException {
    public CannotDeleteVmException(String message){
        super(message);
    }
}
