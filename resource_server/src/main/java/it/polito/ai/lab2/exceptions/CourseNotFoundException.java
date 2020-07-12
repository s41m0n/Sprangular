package it.polito.ai.lab2.exceptions;

public class CourseNotFoundException extends TeamServiceException{

    public CourseNotFoundException(String message) {
        super(message);
    }
}
