package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.dtos.VmModelDTO;
import it.polito.ai.lab2.pojos.UpdateVmDetails;
import it.polito.ai.lab2.pojos.VmModelDetails;
import it.polito.ai.lab2.pojos.VmProfessorDetails;
import it.polito.ai.lab2.pojos.VmStudentDetails;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.util.List;

public interface VmService {

  VmDTO createVm(Long teamId, VmDTO vmDTO);

  VmDTO deleteVm(Long vmId);

  VmDTO updateVmResources(Long vmId, UpdateVmDetails updateVmDetails);

  VmDTO switchVm(Long vmId, boolean active);

  void editVmOwner(Long vmId, String studentId);

  VmDTO getVm(Long vmId);

  Resource getVmInstance(Long vmId) throws FileNotFoundException;

  List<VmStudentDetails> getVmsOfTeam(Long teamId);

  List<VmProfessorDetails> getVmsOfCourse(String courseId);

  List<VmDTO> getVmsOfStudentOfCourse(String studentId, String courseId);

  List<VmDTO> getOwnedVmsOfStudentOfCourse(String studentId, String courseId);
}
