package it.polito.ai.lab2.dtos;

import lombok.Data;

@Data
public class VmDTO {

  Long id;

  String name;

  int vCpu;

  int diskStorage;

  int ram;

  boolean active;

  String imagePath;
}
