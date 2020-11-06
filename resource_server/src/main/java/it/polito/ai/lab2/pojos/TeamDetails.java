package it.polito.ai.lab2.pojos;

import it.polito.ai.lab2.dtos.StudentDTO;
import lombok.Data;

import java.util.List;

@Data
public class TeamDetails {
  Long id;
  String name;
  boolean active;
  int maxVCpu;
  int maxDiskStorage;
  int maxRam;
  int maxActiveInstances;
  int maxTotalInstances;
  List<StudentDTO> members;
}
