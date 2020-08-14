package it.polito.ai.lab2.dtos;

import it.polito.ai.lab2.utility.AssignmentStatus;
import lombok.Data;

import javax.persistence.Id;

@Data
public class AssignmentSolutionDTO {

  @Id
  Long id;

  AssignmentStatus status;

  int grade;
}
