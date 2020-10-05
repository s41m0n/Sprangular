package it.polito.ai.lab2.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AssignmentDTO {

  Long id;

  String name;

  String imagePath;

  LocalDate releaseDate;

  LocalDate dueDate;
}
