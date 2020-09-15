package it.polito.ai.lab2.exceptions;

public class ConfirmEmailTokenNotFoundException extends UserServiceException {
  public ConfirmEmailTokenNotFoundException(String message) {
    super(message);
  }
}
