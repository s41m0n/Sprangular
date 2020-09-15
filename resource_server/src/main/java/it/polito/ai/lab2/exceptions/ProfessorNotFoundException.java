package it.polito.ai.lab2.exceptions;

public class ProfessorNotFoundException extends UserServiceException {
  public ProfessorNotFoundException(String message) {
    super(message);
  }
}
