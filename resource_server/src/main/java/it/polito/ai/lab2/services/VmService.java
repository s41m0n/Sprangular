package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.dtos.VmModelDTO;

import java.util.List;

public interface VmService {

    boolean createVmModel(VmModelDTO vmModelDTO, Long courseId);

    boolean createVm(Long vmModelId, VmDTO vmDTO);

    boolean deleteVm(Long vmId);

    boolean turnOnVm(Long vmId);

    boolean turnOffVm(Long vmId);

    boolean addVmOwner(Long vmId, String studentId);

    List<VmDTO> getAllVms();

    VmDTO getVm(Long vmId);

    List<VmDTO> getVmsOfGroup(Long groupId);

    List<VmDTO> getVmsOfStudent(String studentId);
}
