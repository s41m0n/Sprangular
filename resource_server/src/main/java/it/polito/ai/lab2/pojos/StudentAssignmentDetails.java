package it.polito.ai.lab2.pojos;

import it.polito.ai.lab2.utility.AssignmentStatus;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class StudentAssignmentDetails {
  Long assignmentId;
  String name;
  Timestamp releaseDate;
  Timestamp dueDate;
  Long assignmentSolutionId;
  AssignmentStatus status;
  String grade;
  Timestamp statusTs;
}
