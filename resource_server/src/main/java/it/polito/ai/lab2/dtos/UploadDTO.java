package it.polito.ai.lab2.dtos;

import lombok.Data;

import javax.persistence.Id;
import java.sql.Timestamp;

@Data
public class UploadDTO {

  @Id
  Long id;

  String imagePath;

  Timestamp timestamp;

  String comment;
}
