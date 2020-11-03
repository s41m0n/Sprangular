package it.polito.ai.lab2.exceptions;

public class TokenNotFoundException extends TeamServiceException{
  public TokenNotFoundException(String message) {
    super(message);
  }
}
