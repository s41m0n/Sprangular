package it.polito.ai.lab2.exceptions;

public class StudentNotInCourseException extends UserServiceException {
    public StudentNotInCourseException(String message) {
        super(message);
    }
}
