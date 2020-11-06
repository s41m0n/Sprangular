package it.polito.ai.lab2.exceptions;

public class DuplicateStudentInTeamException extends TeamServiceException {
  public DuplicateStudentInTeamException(String message) {
    super(message);
  }
}
