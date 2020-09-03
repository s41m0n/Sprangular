package it.polito.ai.lab2.exceptions;

public class UserNotFoundException extends UserServiceException{
    public UserNotFoundException(String message){
        super(message);
    }
}
