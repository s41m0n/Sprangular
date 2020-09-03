package it.polito.ai.lab2.exceptions;

public class CourseNotFoundException extends CourseServiceException {
    public CourseNotFoundException(String message) {
        super(message);
    }
}
