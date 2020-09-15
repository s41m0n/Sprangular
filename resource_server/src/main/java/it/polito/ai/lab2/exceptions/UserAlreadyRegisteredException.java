package it.polito.ai.lab2.exceptions;

public class UserAlreadyRegisteredException extends UserServiceException {
  public UserAlreadyRegisteredException(String message) {
    super(message);
  }
}
