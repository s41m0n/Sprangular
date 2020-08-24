package it.polito.ai.lab2.exceptions;

public class CourseNotEmptyException extends TeamServiceException {
    public CourseNotEmptyException(String message) {
        super(message);
    }
}
