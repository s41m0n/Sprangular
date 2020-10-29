package it.polito.ai.lab2.exceptions;

public class DuplicatedCourseException extends CourseServiceException {
  public DuplicatedCourseException(String message) {
    super(message);
  }
}
