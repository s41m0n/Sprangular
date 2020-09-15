package it.polito.ai.lab2.exceptions;

public class CourseNotEnabledException extends CourseServiceException {
  public CourseNotEnabledException(String message) {
    super(message);
  }
}
