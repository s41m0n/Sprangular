package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.dtos.VmModelDTO;

import java.util.List;

public interface VmService {

    boolean createVmModel(VmModelDTO vmModelDTO, String courseName);

    boolean createVm(Long vmModelId, VmDTO vmDTO, String ownerId, String courseName);

    boolean deleteVm(Long vmId);

    boolean updateVmResourceLimits(Long teamId, int vCpu, int diskStorage, int ram, int maxActiveInstances, int maxTotalInstances);

    boolean updateVmResources(Long vmId, int vCpu, int diskStorage, int ram);

    boolean turnOnVm(Long vmId);

    boolean turnOffVm(Long vmId);

    boolean addVmOwner(Long vmId, String studentId);

    List<VmDTO> getAllVms();

    VmDTO getVm(Long vmId);

    List<VmDTO> getVmsOfGroup(Long groupId);

    List<VmDTO> getVmsOfStudent(String studentId);

    List<VmDTO> getVmsOfCourse(String courseName);

    List<VmDTO> getVmsOfStudentOfCourse(String studentId, String courseName);
}
