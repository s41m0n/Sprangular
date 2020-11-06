package it.polito.ai.lab2.exceptions;

public class CourseProfessorNotAssignedException extends CourseServiceException {
  public CourseProfessorNotAssignedException(String message) {
    super(message);
  }
}
