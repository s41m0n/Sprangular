package it.polito.ai.lab2.pojos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;

@Data
public class AssignmentDetails {

  String name;

  Timestamp releaseDate;

  Timestamp dueDate;

  MultipartFile document;
}
