package it.polito.ai.lab2.exceptions;

public class VmModelNotFoundException extends VmServiceException {
  public VmModelNotFoundException(String message) {
    super(message);
  }
}
