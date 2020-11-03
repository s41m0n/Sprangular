package it.polito.ai.lab2.exceptions;

public class TeamNotActiveException extends TeamServiceException {
  public TeamNotActiveException(String message) {
    super(message);
  }
}
