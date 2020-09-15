package it.polito.ai.lab2.exceptions;

public class AssignmentSolutionNotReviewedException extends AssignmentAndUploadServiceException {
  public AssignmentSolutionNotReviewedException(String message) {
    super(message);
  }
}
