package it.polito.ai.lab2.exceptions;

public class UserNotVerifiedException extends UserServiceException {
  public UserNotVerifiedException(String message) {
    super(message);
  }
}
