package it.polito.ai.lab2.pojos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class AssignmentDetails {

  String name;

  LocalDate releaseDate;

  LocalDate dueDate;

  MultipartFile document;
}
