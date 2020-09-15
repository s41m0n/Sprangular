package it.polito.ai.lab2.exceptions;

public class VmModelAlreadyPresentException extends VmServiceException {
  public VmModelAlreadyPresentException(String message) {
    super(message);
  }
}
