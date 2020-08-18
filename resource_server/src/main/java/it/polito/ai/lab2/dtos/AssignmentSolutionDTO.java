package it.polito.ai.lab2.dtos;

import it.polito.ai.lab2.utility.AssignmentStatus;
import lombok.Data;

@Data
public class AssignmentSolutionDTO {

  Long id;

  AssignmentStatus status;

  int grade;
}
