package it.polito.ai.lab2.utility;

import java.sql.Timestamp;
import java.util.Calendar;

public class Utility {

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
