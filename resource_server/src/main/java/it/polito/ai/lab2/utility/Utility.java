package it.polito.ai.lab2.utility;

import lombok.extern.java.Log;

import java.nio.file.Path;
import java.nio.file.Paths;

@Log(topic = "Utility")
public class Utility {

  // File storage directories
  public static final String IMAGES_ROOT_DIR = "uploads";
  public static final Path PHOTOS_DIR = Paths.get(IMAGES_ROOT_DIR).resolve("photos");
  public static final Path ASSIGNMENTS_DIR = Paths.get(IMAGES_ROOT_DIR).resolve("assignments");
  public static final Path UPLOADS_DIR = Paths.get(IMAGES_ROOT_DIR).resolve("uploads");
  public static final Path VM_MODELS_DIR = Paths.get(IMAGES_ROOT_DIR).resolve("vm-models");
  public static final Path VMS_DIR = Paths.get(IMAGES_ROOT_DIR).resolve("vms");

  // Roles
  public static final String PROFESSOR_ROLE = "ROLE_PROFESSOR";
  public static final String STUDENT_ROLE = "ROLE_STUDENT";

  public static ProposalStatus deletedStatusOf(ProposalStatus proposalStatus) {
    switch (proposalStatus) {
      case ACCEPTED:
        return ProposalStatus.ACCEPTED_DELETED;
      case PENDING:
        return ProposalStatus.PENDING_DELETED;
      case REJECTED:
        return ProposalStatus.REJECTED_DELETED;
      default: throw new IllegalArgumentException("Unexpected value: " + proposalStatus.toString());
    }
  }

  public static boolean isProposalDeleted(ProposalStatus proposalStatus) {
    return proposalStatus.equals(ProposalStatus.ACCEPTED_DELETED)
        || proposalStatus.equals(ProposalStatus.REJECTED_DELETED)
        || proposalStatus.equals(ProposalStatus.PENDING_DELETED);
  }

  public static String proposalStatusString(ProposalStatus proposalStatus) {
    switch (proposalStatus) {
      case ACCEPTED:
      case ACCEPTED_DELETED:
        return "ACCEPTED";
      case PENDING:
      case PENDING_DELETED:
        return "PENDING";
      case REJECTED:
      case REJECTED_DELETED:
        return "REJECTED";
      default: throw new IllegalArgumentException("Unexpected value: " + proposalStatus.toString());
    }
  }
}
