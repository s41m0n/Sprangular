package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.dtos.VmModelDTO;

import java.util.List;
import java.util.Optional;

public interface VmService {

    boolean createVmModel(VmModelDTO vmModelDTO, String courseId);

    boolean createVm(Long vmModelId, VmDTO vmDTO, String courseId);

    boolean deleteVm(Long vmId);

    boolean updateVmResources(Long vmId, int vCpu, int diskStorage, int ram);

    boolean turnOnVm(Long vmId);

    boolean turnOffVm(Long vmId);

    boolean addVmOwner(Long vmId, String studentId);

    Optional<VmDTO> getVm(Long vmId);

    List<VmDTO> getVmsOfTeam(Long teamId);

    List<VmDTO> getVmsOfCourse(String courseId);

    List<VmDTO> getVmsOfStudentOfCourse(String studentId, String courseId);

    List<VmDTO> getOwnedVmsOfStudentOfCourse(String studentId, String courseId);
}
