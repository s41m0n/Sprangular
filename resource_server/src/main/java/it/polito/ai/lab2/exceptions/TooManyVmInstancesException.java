package it.polito.ai.lab2.exceptions;

public class TooManyVmInstancesException extends VmServiceException {
    public TooManyVmInstancesException(String message){
        super(message);
    }
}
