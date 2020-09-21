package it.polito.ai.lab2.exceptions;

public class UserServiceException extends RuntimeException {
  public UserServiceException(String message) {
    super(message);
  }
}
