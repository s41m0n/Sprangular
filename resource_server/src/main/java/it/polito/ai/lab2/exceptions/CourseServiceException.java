package it.polito.ai.lab2.exceptions;

public class CourseServiceException extends RuntimeException {
  public CourseServiceException(String message) {
    super(message);
  }
}
