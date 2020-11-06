package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.exceptions.*;
import it.polito.ai.lab2.pojos.UpdateVmDetails;
import it.polito.ai.lab2.pojos.VmProfessorDetails;
import it.polito.ai.lab2.pojos.VmStudentDetails;
import it.polito.ai.lab2.repositories.*;
import it.polito.ai.lab2.utility.Utility;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VmServiceImpl implements VmService {

  @Autowired
  ModelMapper modelMapper;

  @Autowired
  CourseRepository courseRepository;

  @Autowired
  VmModelRepository vmModelRepository;

  @Autowired
  VmRepository vmRepository;

  @Autowired
  StudentRepository studentRepository;

  @Autowired
  TeamRepository teamRepository;

  @Override
  @PreAuthorize("hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentInTeam(#teamId)")
  public VmDTO createVm(Long teamId, VmDTO vmDTO) {
    Team team = teamRepository.findById(teamId).orElseThrow(
        () -> new TeamNotFoundException("Team " + teamId + " does not exist"));
    Student owner = studentRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName())
        .orElseThrow(() -> new StudentNotFoundException(
            "Student " + SecurityContextHolder.getContext().getAuthentication().getName() + " does not exist"));

    if(!team.isActive()) {
      throw new TeamNotActiveException("Team " + teamId + " is not active");
    }

    if (!owner.getCourses().contains(team.getCourse())) { //student not enrolled in course
      throw new StudentNotInCourseException(
          "Student " + owner.getId() + " is not enrolled in course " + team.getCourse().getAcronym());
    }

    if (!team.getCourse().isEnabled()) { //course not enabled
      throw new CourseNotEnabledException("Course " + team.getCourse().getAcronym() + " is not enabled");
    }

    if (owner.getTeams().isEmpty()) { //student in no teams
      throw new StudentNotInTeamException("Student " + owner.getId() + " does not belong to any team");
    }

    if (team.getVms().size() + 1 > team.getMaxTotalInstances()) {
      throw new TooManyVmInstancesException("Cannot create the new VM, too many instances");
    }

    int actualVCpu = 0;
    int actualRam = 0;
    int actualDiskStorage = 0;
    int numOfActiveVms = 0;

    for (Vm vm : team.getVms()) {
      actualVCpu += vm.getVCpu();
      actualRam += vm.getRam();
      actualDiskStorage += vm.getDiskStorage();
      if (vm.isActive()) {
        numOfActiveVms += 1;
      }
    }

    if (actualVCpu + vmDTO.getVCpu() > team.getMaxVCpu()
        || actualRam + vmDTO.getRam() > team.getMaxRam()
        || actualDiskStorage + vmDTO.getDiskStorage() > team.getMaxDiskStorage()) {
      throw new MaxVmResourcesException("Cannot create the VM, no more resources available");
    }

    if (vmDTO.isActive() && numOfActiveVms + 1 > team.getMaxActiveInstances()) {
      throw new MaxVmResourcesException("Cannot create the VM, no more resources available");
    }

    Vm vm = new Vm();
    vm.setVmModel(team.getCourse().getVmModel());
    vm.setName(vmDTO.getName());
    vm.setActive(vmDTO.isActive());
    vm.setVCpu(vmDTO.getVCpu());
    vm.setRam(vmDTO.getRam());
    vm.setDiskStorage(vmDTO.getDiskStorage());
    vm.setTeam(team);
    vm.addOwner(owner);
    Vm savedVm = vmRepository.save(vm);
    Path vmPath = Utility.VMS_DIR.resolve(savedVm.getId().toString());
    savedVm.setImagePath(vmPath.toString());
    try {
      Files.copy(Paths.get(vm.getVmModel().getImagePath()), vmPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Cannot store the file: " + e.getMessage());
    }
    return modelMapper.map(savedVm, VmDTO.class);
  }

  @Override
  @PreAuthorize("hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentOwnerOfVm(#vmId)")
  public VmDTO deleteVm(Long vmId) {
    Vm vm = vmRepository.findById(vmId)
        .orElseThrow(() -> new VmNotFoundException("Vm " + vmId + " does not exist"));

    if (!vm.isActive()) {
      vmRepository.delete(vm);
      try {
        Files.delete(Paths.get(vm.getImagePath()));
      } catch (IOException e) {
        throw new RuntimeException("Cannot delete the file: " + e.getMessage());
      }
      return modelMapper.map(vm, VmDTO.class);
    }
    throw new CannotDeleteVmException("VM " + vmId + " cannot be deleted. VM is still active");
  }

  @Override
  @PreAuthorize("hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentOwnerOfVm(#vmId)")
  public VmDTO updateVmResources(Long vmId, UpdateVmDetails updateVmDetails) {
    Vm vm = vmRepository.findById(vmId).orElseThrow(() -> new VmNotFoundException("Vm " + vmId + " does not exist"));

    if (vm.isActive()) {
      throw new VmIsActiveException("Vm " + vmId + " is active and cannot be updated. Please turn it off");
    }

    Team team = vm.getTeam();

    int actualVCpu = 0;
    int actualRam = 0;
    int actualDiskStorage = 0;

    for (Vm v : team.getVms()) {
      actualVCpu += v.getVCpu();
      actualRam += v.getRam();
      actualDiskStorage += v.getDiskStorage();
    }

    if (actualVCpu - vm.getVCpu() + updateVmDetails.getVCpu() > team.getMaxVCpu()
        || actualRam - vm.getRam() + updateVmDetails.getRam() > team.getMaxRam()
        || actualDiskStorage - vm.getDiskStorage() + updateVmDetails.getDiskStorage() > team.getMaxDiskStorage()) {
      throw new MaxVmResourcesException("Cannot update VM " + vmId + " no more resources available");
    }

    vm.setVCpu(updateVmDetails.getVCpu());
    vm.setRam(updateVmDetails.getRam());
    vm.setDiskStorage(updateVmDetails.getDiskStorage());
    vmRepository.save(vm);
    return modelMapper.map(vm, VmDTO.class);
  }

  @Override
  @PreAuthorize("hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentOwnerOfVm(#vmId)")
  public VmDTO switchVm(Long vmId, boolean active) {
    Vm vm = vmRepository.findById(vmId).orElseThrow(() -> new VmNotFoundException("Vm " + vmId + " does not exist"));
    if (active && vm.isActive()) {
      return modelMapper.map(vm, VmDTO.class);
    } else if (active) {
      if (vm.getTeam().getVms().stream().filter(Vm::isActive).count() == vm.getTeam().getMaxActiveInstances()) {
        throw new MaxVmResourcesException("Cannot turn on VM " + vmId + " too many active VMs");
      }
    }
    vm.setActive(active);
    return modelMapper.map(vm, VmDTO.class);
  }

  @Override
  @PreAuthorize("hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentOwnerOfVm(#vmId)")
  public void editVmOwner(Long vmId, String studentId) {
    Vm vm = vmRepository.findById(vmId).orElseThrow(() -> new VmNotFoundException("Vm " + vmId + " does not exist"));
    Student student = studentRepository.findById(studentId).orElseThrow(
        () -> new StudentNotFoundException("Student " + studentId + " does not exist"));

    if (!student.isVerified()) {
      throw new UserNotVerifiedException("Student " + studentId + " is not verified");
    }

    if (!vm.getOwners().contains(student)) {
      vm.addOwner(student);
    } else {
      vm.removeOwner(student);
    }
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') " +
      "or hasRole('ROLE_STUDENT') and @securityServiceImpl.isVmOfStudentTeam(#vmId)")
  public VmDTO getVm(Long vmId) {
    Vm vm = vmRepository.findById(vmId).orElseThrow(() -> new VmNotFoundException("Vm " + vmId + " does not exist"));

    return modelMapper.map(vm, VmDTO.class);
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') " +
      "or hasRole('ROLE_STUDENT') and @securityServiceImpl.isVmOfStudentTeam(#vmId)")
  public Resource getVmInstance(Long vmId) throws FileNotFoundException {
    Vm vm = vmRepository.findById(vmId).orElseThrow(() -> new VmNotFoundException("Vm " + vmId + " does not exist"));
    if (!vm.isActive()) {
      throw new InactiveVmException("Vm " + vmId + " is not active");
    }
    Resource file = null;
    try {
      file = new UrlResource(Paths.get(vm.getImagePath()).toUri());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    if (file == null)
      throw new FileNotFoundException("Vm instance " + vmId + " not found");
    return file;
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isTeamOfProfessorCourse(#teamId) " +
      "or hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentInTeam(#teamId)")
  public List<VmStudentDetails> getVmsOfTeam(Long teamId) {
    return teamRepository.findById(teamId)
        .map(team -> team.getVms().stream()
            .map(vm -> {
              List<StudentDTO> sDtos = vm.getOwners().stream()
                  .map(s -> modelMapper.map(s, StudentDTO.class))
                  .collect(Collectors.toList());
              VmStudentDetails vsd = new VmStudentDetails();
              vsd.setOwners(sDtos);
              vsd.setVm(modelMapper.map(vm, VmDTO.class));
              return vsd;
            })
            .collect(Collectors.toList()))
        .orElseThrow(() -> new TeamNotFoundException("Team " + teamId + " does not exist"));
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId)")
  public List<VmProfessorDetails> getVmsOfCourse(String courseId) {
    Course course = courseRepository.findById(courseId)
        .orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));
    return course.getTeams().stream()
        .filter(Team::isActive)
        .map(t -> {
          List<VmDTO> vmDtos = t.getVms().stream()
              .map(vm -> modelMapper.map(vm, VmDTO.class))
              .collect(Collectors.toList());
          VmProfessorDetails vpd = new VmProfessorDetails();
          vpd.setVms(vmDtos);
          vpd.setTeam(modelMapper.map(t, TeamDTO.class));
          return vpd;
        })
        .collect(Collectors.toList());
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId) " +
      "or hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentSelf(#studentId) " +
      "and @securityServiceImpl.isStudentEnrolled(#courseId)")
  public List<VmDTO> getVmsOfStudentOfCourse(String studentId, String courseId) {
    courseRepository.findById(courseId).orElseThrow(
        () -> new CourseNotFoundException("Course " + courseId + " does not exist"));
    Student student = studentRepository.findById(studentId).orElseThrow(
        () -> new StudentNotFoundException("Student " + studentId + " does not exist"));

    Team team = null;
    for (Team t : student.getTeams()) { //find the student's team of the selected course
      if (t.getCourse().getName().equals(courseId)) {
        team = t;
        break;
      }
    }

    if (team == null) {
      throw new StudentNotInTeamOfCourseException(
          "Student " + studentId + "does not belong to a team of course " + courseId);
    }

    return team.getVms().stream()
        .map(vm -> modelMapper.map(vm, VmDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  @PreAuthorize("hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseId) " +
      "or hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentSelf(#studentId) " +
      "and @securityServiceImpl.isStudentEnrolled(#courseId)")
  public List<VmDTO> getOwnedVmsOfStudentOfCourse(String studentId, String courseId) {
    courseRepository.findById(courseId).orElseThrow(
        () -> new CourseNotFoundException("Course " + courseId + " does not exist"));
    Student student = studentRepository.findById(studentId).orElseThrow(
        () -> new StudentNotFoundException("Student " + studentId + " does not exist"));

    Team team = null;
    for (Team t : student.getTeams()) { //find the student's team of the selected course
      if (t.getCourse().getName().equals(courseId)) {
        team = t;
        break;
      }
    }

    if (team == null) {
      throw new StudentNotInTeamOfCourseException(
          "Student " + studentId + "does not belong to a team of course " + courseId);
    }

    return student.getOwnedVms().stream()
        .filter(vm -> vm.getTeam().getCourse().getName().equals(courseId))
        .map(vm -> modelMapper.map(vm, VmDTO.class))
        .collect(Collectors.toList());
  }
}
