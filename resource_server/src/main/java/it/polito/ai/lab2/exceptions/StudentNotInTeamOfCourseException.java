package it.polito.ai.lab2.exceptions;

public class StudentNotInTeamOfCourseException extends TeamServiceException {
  public StudentNotInTeamOfCourseException(String message) {
    super(message);
  }
}
