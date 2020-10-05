package it.polito.ai.lab2.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class StudentUpload {

  @Id
  @GeneratedValue
  Long id;

  String imagePath;

  LocalDateTime timestamp;

  String comment;

  @ManyToOne
  @JoinColumn(name = "assignmentSolution_id")
  AssignmentSolution assignmentSolution;

  @OneToOne
  @JoinColumn(name = "teacherUpload_id")
  ProfessorUpload teacherRevision;
}
