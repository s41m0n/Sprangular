package it.polito.ai.lab2.pojos;

import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.dtos.VmDTO;
import lombok.Data;

import java.util.List;

@Data
public class VmProfessorDetails {
  TeamDTO team;
  List<VmDTO> vms;
}
