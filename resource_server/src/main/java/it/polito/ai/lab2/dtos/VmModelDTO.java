package it.polito.ai.lab2.dtos;

import lombok.Data;

import javax.persistence.Id;

@Data
public class VmModelDTO {

  @Id
  Long id;

  String name;

  String imagePath;
}
