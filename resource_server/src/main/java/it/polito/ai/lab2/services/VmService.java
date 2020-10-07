package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.dtos.VmModelDTO;
import it.polito.ai.lab2.pojos.UpdateVmDetails;
import it.polito.ai.lab2.pojos.VmModelDetails;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.util.List;

public interface VmService {

  VmModelDTO createVmModel(VmModelDetails vmModelDetails, String courseId);

  VmModelDTO updateVmModel(VmModelDetails vmModelDetails, String courseId);

  VmDTO createVm(Long teamId, VmDTO vmDTO);

  VmDTO deleteVm(Long vmId);

  VmDTO updateVmResources(Long vmId, UpdateVmDetails updateVmDetails);

  VmDTO turnOnVm(Long vmId);

  VmDTO turnOffVm(Long vmId);

  void addVmOwner(Long vmId, String studentId);

  VmDTO getVm(Long vmId);

  Resource getVmInstance(Long vmId) throws FileNotFoundException;

  List<VmDTO> getVmsOfTeam(Long teamId);

  List<VmDTO> getVmsOfCourse(String courseId);

  List<VmDTO> getVmsOfStudentOfCourse(String studentId, String courseId);

  List<VmDTO> getOwnedVmsOfStudentOfCourse(String studentId, String courseId);
}
