package it.polito.ai.lab2.exceptions;

public class UploadNotAllowedException extends AssignmentAndUploadServiceException {
  public UploadNotAllowedException(String message) {
    super(message);
  }
}
