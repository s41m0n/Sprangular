package it.polito.ai.lab2.utility;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Calendar;

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

  public static String timestampToCronTrigger(Timestamp timestamp) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(timestamp.getTime());
    return calendar.get(Calendar.MINUTE)
        + " " + calendar.get(Calendar.HOUR)
        + " " + calendar.get(Calendar.DAY_OF_MONTH)
        + " " + (calendar.get(Calendar.MONTH) + 1)
        + " *"
        + " " + calendar.get(Calendar.YEAR);
  }
}
