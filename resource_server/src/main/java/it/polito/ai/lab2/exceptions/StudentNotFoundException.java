package it.polito.ai.lab2.exceptions;

public class StudentNotFoundException extends UserServiceException {
  public StudentNotFoundException(String message) {
    super(message);
  }
}
