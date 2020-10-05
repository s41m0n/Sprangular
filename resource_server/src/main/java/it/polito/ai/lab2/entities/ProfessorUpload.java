package it.polito.ai.lab2.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
@Data
public class ProfessorUpload {

  @Id
  @GeneratedValue
  Long id;

  String imagePath;

  LocalDateTime timestamp;

  String comment;

  @OneToOne(mappedBy = "teacherRevision")
  StudentUpload revisedSolution;
}
