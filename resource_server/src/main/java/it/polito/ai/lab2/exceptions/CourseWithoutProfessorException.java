package it.polito.ai.lab2.exceptions;

public class CourseWithoutProfessorException extends CourseServiceException {
  public CourseWithoutProfessorException(String message) {
    super(message);
  }
}
