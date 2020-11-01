package it.polito.ai.lab2.dtos;

import it.polito.ai.lab2.utility.AssignmentStatus;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class AssignmentSolutionDTO {

  Long id;

  AssignmentStatus status;

  String grade;

  Timestamp statusTs;
}
