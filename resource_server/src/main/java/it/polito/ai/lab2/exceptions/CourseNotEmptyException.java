package it.polito.ai.lab2.exceptions;

public class CourseNotEmptyException extends CourseServiceException {
    public CourseNotEmptyException(String message) {
        super(message);
    }
}
