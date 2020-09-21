package it.polito.ai.lab2.exceptions;

public class TeamNameAlreadyInCourseException extends TeamServiceException {
  public TeamNameAlreadyInCourseException(String message) {
    super(message);
  }
}
