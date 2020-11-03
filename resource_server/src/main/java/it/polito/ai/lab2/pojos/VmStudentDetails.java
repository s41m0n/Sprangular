package it.polito.ai.lab2.pojos;

import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.VmDTO;
import lombok.Data;

import java.util.List;

@Data
public class VmStudentDetails {
  VmDTO vm;
  List<StudentDTO> owners;
}
