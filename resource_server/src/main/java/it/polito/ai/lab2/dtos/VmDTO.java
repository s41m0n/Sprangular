package it.polito.ai.lab2.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@Data
public class VmDTO extends RepresentationModel<VmDTO> {

  Long id;

  String name;

  int vCpu;

  int diskStorage;

  int ram;

  boolean active;

  String imagePath;
}
