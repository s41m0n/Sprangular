package it.polito.ai.lab2.exceptions;

public class VmIsActiveException extends VmServiceException {
  public VmIsActiveException(String message) {
    super(message);
  }
}
