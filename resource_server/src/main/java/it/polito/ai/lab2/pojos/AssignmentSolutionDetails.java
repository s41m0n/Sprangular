package it.polito.ai.lab2.pojos;

import it.polito.ai.lab2.utility.AssignmentStatus;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class AssignmentSolutionDetails {
  Long id;
  String studentName;
  String studentSurname;
  String studentId;
  AssignmentStatus status;
  Timestamp statusTs;
  String grade;
}
