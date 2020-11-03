package it.polito.ai.lab2.exceptions;

public class CannotUpdateCourseException extends CourseServiceException {
  public CannotUpdateCourseException(String message){
    super(message);
  }
}
