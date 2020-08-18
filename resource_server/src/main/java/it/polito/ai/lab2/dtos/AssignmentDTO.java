package it.polito.ai.lab2.dtos;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class AssignmentDTO {

  Long id;

  String name;

  String imagePath;

  Timestamp releaseDate;

  Timestamp dueDate;
}
