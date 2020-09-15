package it.polito.ai.lab2.exceptions;

public class MaxVmResourcesException extends VmServiceException {
  public MaxVmResourcesException(String message) {
    super(message);
  }
}
