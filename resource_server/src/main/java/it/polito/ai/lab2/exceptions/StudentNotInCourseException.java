package it.polito.ai.lab2.exceptions;

public class StudentNotInCourseException extends RuntimeException{

    public StudentNotInCourseException(String message) {
        super(message);
    }
}
