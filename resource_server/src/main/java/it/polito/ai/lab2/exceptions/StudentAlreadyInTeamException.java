package it.polito.ai.lab2.exceptions;

public class StudentAlreadyInTeamException extends TeamServiceException {
  public StudentAlreadyInTeamException(String message) {
    super(message);
  }
}
