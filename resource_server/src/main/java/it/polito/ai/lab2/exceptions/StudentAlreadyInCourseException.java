package it.polito.ai.lab2.exceptions;

public class StudentAlreadyInCourseException extends CourseServiceException {
  public StudentAlreadyInCourseException(String message) { super(message); }
}
