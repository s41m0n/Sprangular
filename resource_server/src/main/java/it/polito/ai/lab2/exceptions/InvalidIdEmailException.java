package it.polito.ai.lab2.exceptions;

public class InvalidIdEmailException extends UserServiceException {
  public InvalidIdEmailException(String message) {
    super(message);
  }
}
