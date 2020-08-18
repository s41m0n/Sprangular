package it.polito.ai.lab2.dtos;

import lombok.Data;

import javax.persistence.Id;

@Data
public class VmDTO {

  @Id
  Long id;

  int vCpu;

  int diskStorage;

  int ram;

  boolean active;

  String imagePath;
}
