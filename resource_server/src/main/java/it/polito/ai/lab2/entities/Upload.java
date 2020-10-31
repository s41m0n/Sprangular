package it.polito.ai.lab2.entities;

import it.polito.ai.lab2.utility.AssignmentStatus;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
public class Upload {

  @Id
  @GeneratedValue
  Long id;

  String imagePath;

  Timestamp timestamp;

  String comment;

  @Enumerated(EnumType.STRING)
  AssignmentStatus status;

  @ManyToOne
  @JoinColumn(name = "assignmentSolution_id")
  AssignmentSolution assignmentSolution;
}
