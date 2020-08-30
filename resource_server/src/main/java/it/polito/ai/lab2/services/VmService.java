package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.dtos.VmModelDTO;

import java.util.List;
import java.util.Optional;

public interface VmService {

    boolean createVmModel(VmModelDTO vmModelDTO, String courseName);

    boolean createVm(Long vmModelId, VmDTO vmDTO, String ownerId, String courseName);

    boolean deleteVm(Long vmId);

    boolean updateVmResources(Long vmId, int vCpu, int diskStorage, int ram);

    boolean turnOnVm(Long vmId);

    boolean turnOffVm(Long vmId);

    boolean addVmOwner(Long vmId, String studentId);

    Optional<VmDTO> getVm(Long vmId);

    List<VmDTO> getVmsOfTeam(Long teamId);

    List<VmDTO> getVmsOfCourse(String courseName);

    List<VmDTO> getVmsOfStudentOfCourse(String studentId, String courseName);

    List<VmDTO> getOwnedVmsOfStudentOfCourse(String studentId, String courseName);
}
