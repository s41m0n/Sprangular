package it.polito.ai.lab2.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UploadDTO {

  Long id;

  String imagePath;

  LocalDateTime timestamp;

  String comment;
}
