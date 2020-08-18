package it.polito.ai.lab2.dtos;

import lombok.Data;

import javax.persistence.Id;
import java.sql.Timestamp;

@Data
public class AssignmentDTO {

  @Id
  Long id;

  String name;

  String imagePath;

  Timestamp releaseDate;

  Timestamp dueDate;
}
