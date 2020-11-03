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
  public static final String ADMIN_ROLE = "ROLE_ADMIN";
  public static final String PROFESSOR_ROLE = "ROLE_PROFESSOR";
  public static final String STUDENT_ROLE = "ROLE_STUDENT";
}
