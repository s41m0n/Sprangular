package it.polito.ai.lab2.exceptions;

public class UserRoleNotFoundException extends UserServiceException {
  public UserRoleNotFoundException(String message) {
    super(message);
  }
}
