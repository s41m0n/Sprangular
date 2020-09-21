package it.polito.ai.lab2.exceptions;

public class TooManyActualResourcesException extends VmServiceException {
  public TooManyActualResourcesException(String message) {
    super(message);
  }
}
