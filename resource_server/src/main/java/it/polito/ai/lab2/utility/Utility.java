package it.polito.ai.lab2.utility;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Calendar;

public class Utility {

  public static final String imagesRootDir = "uploads";
  public static final Path photosDir = Paths.get(imagesRootDir).resolve("photos");
  public static final Path assignmentsDir = Paths.get(imagesRootDir).resolve("assignments");
  public static final Path assignmentSolutionsDir = Paths.get(imagesRootDir).resolve("assignment-solutions");
  public static final Path uploadsDir = Paths.get(imagesRootDir).resolve("uploads");
  public static final Path vmModelsDir = Paths.get(imagesRootDir).resolve("vm-models");
  public static final Path vmsDir = Paths.get(imagesRootDir).resolve("vms");

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
