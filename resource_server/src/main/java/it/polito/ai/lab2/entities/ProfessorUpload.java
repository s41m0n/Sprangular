package it.polito.ai.lab2.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
public class ProfessorUpload {

  @Id
  @GeneratedValue
  Long id;

  String imagePath;

  Timestamp timestamp;

  String comment;

  @OneToOne
  StudentUpload revisedSolution;
}
