package it.polito.ai.lab2.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.sql.Timestamp;

@Entity
@Data
public class StudentUpload {

  @Id
  @GeneratedValue
  Long id;

  String imagePath;

  Timestamp timestamp;

  String comment;

  @OneToOne
  ProfessorUpload teacherRevision;
}
