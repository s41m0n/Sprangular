package it.polito.ai.lab2.dtos;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UploadDTO {

  Long id;

  String imagePath;

  Timestamp timestamp;

  String comment;
}
