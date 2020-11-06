package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.pojos.UpdateVmDetails;
import it.polito.ai.lab2.pojos.VmProfessorDetails;
import it.polito.ai.lab2.pojos.VmStudentDetails;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.util.List;

public interface VmService {

  /**
   *
   * @param teamId The team id
   * @param vmDTO The vm
   * @return The created vm
   */
  VmDTO createVm(Long teamId, VmDTO vmDTO);

  /**
   *
   * @param vmId The vm id
   * @return The deleted vm
   */
  VmDTO deleteVm(Long vmId);

  /**
   *
   * @param vmId The vm id
   * @param updateVmDetails The information to update the vm
   * @return The updated vm
   */
  VmDTO updateVmResources(Long vmId, UpdateVmDetails updateVmDetails);

  /**
   *
   * @param vmId The vm id
   * @param active The desired vm state
   * @return The updated vm
   */
  VmDTO switchVm(Long vmId, boolean active);

  /**
   *
   * @param vmId The vm id
   * @param studentId The student id to add/remove
   */
  void editVmOwner(Long vmId, String studentId);

  /**
   *
   * @param vmId The vm id
   * @return The vm
   */
  VmDTO getVm(Long vmId);

  /**
   *
   * @param vmId The vm id
   * @return The vm image
   * @throws FileNotFoundException If the file does not exist
   */
  Resource getVmInstance(Long vmId) throws FileNotFoundException;

  /**
   *
   * @param teamId The team id
   * @return The enriched information of the vms for the team for the student
   */
  List<VmStudentDetails> getVmsOfTeam(Long teamId);

  /**
   *
   * @param courseId The course acronym
   * @return The enriched information of the vms for the course for the professor
   */
  List<VmProfessorDetails> getVmsOfCourse(String courseId);

  /**
   *
   * @param studentId The student id
   * @param courseId The course acronym
   * @return All the vms of the student team in the course
   */
  List<VmDTO> getVmsOfStudentOfCourse(String studentId, String courseId);

  /**
   *
   * @param studentId The student id
   * @param courseId The course acronym
   * @return All the vms owned by the student in the course
   */
  List<VmDTO> getOwnedVmsOfStudentOfCourse(String studentId, String courseId);
}
