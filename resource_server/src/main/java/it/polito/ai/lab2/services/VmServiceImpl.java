package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.dtos.VmModelDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.exceptions.*;
import it.polito.ai.lab2.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VmServiceImpl implements VmService{

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
    public boolean createVmModel(VmModelDTO vmModelDTO, String courseName) {
        Course course = courseRepository.findByName(courseName).orElseThrow(() -> new CourseNotFoundException("Course " + courseName + " does not exist"));
        VmModel v = new VmModel();
        v.setName(vmModelDTO.getName());
        v.setImagePath(vmModelDTO.getImagePath());
        v.setCourse(course);
        vmModelRepository.save(v);
        return true;
    }

    @Override
    public boolean createVm(Long vmModelId, VmDTO vmDTO, String ownerId, String courseName) {
        VmModel vmModel = vmModelRepository.findById(vmModelId).orElseThrow(() -> new VmModelNotFoundException("VmModel " + vmModelId + " does not exist"));
        Student owner = studentRepository.findById(ownerId).orElseThrow(() -> new StudentNotFoundException("Student " + ownerId + " does not exist"));
        Course course = courseRepository.findByName(courseName).orElseThrow(() -> new CourseNotFoundException("Course " + courseName + " does not exist"));

        if(!owner.getCourses().contains(course)){ //student not enrolled in course
            throw new StudentNotInCourseException("Student " + ownerId + " is not enrolled in course " + courseName);
        }

        if(!course.isEnabled()){ //course not enabled
            throw new CourseNotEnabledException("Course " + courseName + " is not enabled");
        }

        if(owner.getTeams().isEmpty()){ //student in no teams
            throw new StudentNotInTeamException("Student " + ownerId + " does not belong to any team");
        }

        Team team = null;
        for(Team t : owner.getTeams()){ //find the student's team of the selected course
            if(t.getCourse().getName().equals(courseName)){
                team = t;
                break;
            }
        }

        if(team == null){
            throw new StudentNotInTeamOfCourseException("Student " + ownerId + "does not belong to a team of course " + courseName);
        }

        if(team.getVms().size() + 1 > team.getMaxTotalInstances()){
            throw new TooManyVmInstancesException("Cannot create the new VM, too many instances");
        }

        int actualVCpu = 0;
        int actualRam = 0;
        int actualDiskStorage = 0;
        int numOfActiveVms = 0;

        for(Vm vm : team.getVms()){
            actualVCpu += vm.getVCpu();
            actualRam += vm.getRam();
            actualDiskStorage += vm.getDiskStorage();
            if(vm.isActive()){
                numOfActiveVms += 1;
            }
        }

        if(actualVCpu + vmDTO.getVCpu() > team.getMaxVCpu() || actualRam + vmDTO.getRam() > team.getMaxRam() || actualDiskStorage + vmDTO.getDiskStorage() > team.getMaxDiskStorage()){
            throw new MaxVmResourcesException("Cannot create the VM, no more resources available");
        }

        if(vmDTO.isActive() && numOfActiveVms + 1 > team.getMaxActiveInstances()){
            throw new MaxVmResourcesException("Cannot create the VM, no more resources available");
        }

        Vm vm = new Vm();
        vm.setVmModel(vmModel);
        vm.setActive(vmDTO.isActive());
        vm.setVCpu(vmDTO.getVCpu());
        vm.setRam(vmDTO.getRam());
        vm.setDiskStorage(vmDTO.getDiskStorage());
        vm.setImagePath(vmDTO.getImagePath());
        vm.setTeam(team);
        vm.setOwner(owner);
        vmRepository.save(vm);
        return true;
    }

    @Override
    public boolean deleteVm(Long vmId) {
        Vm vm = vmRepository.findById(vmId).orElseThrow(() -> new VmNotFoundException("Vm " + vmId + " does not exist"));

        if (vm.getOwners().contains(studentRepository.getOne(SecurityContextHolder.getContext().getAuthentication().getName())) && !vm.isActive()) {
            vmRepository.delete(vm);
            return true;
        }
        throw new CannotDeleteVmException("VM " + vmId + " cannot be deleted. VM is still active or you are not one of the owners");
    }

    @Override
    public boolean updateVmResourceLimits(Long teamId, int vCpu, int diskStorage, int ram, int maxActiveInstances, int maxTotalInstances) { //TODO:only professor
        return false;
    }

    @Override
    public boolean updateVmResources(Long vmId, int vCpu, int diskStorage, int ram) { //only owners
        return false;
    }

    @Override
    public boolean turnOnVm(Long vmId) {
        return false;
    }

    @Override
    public boolean turnOffVm(Long vmId) {
        return false;
    }

    @Override
    public boolean addVmOwner(Long vmId, String studentId) {
        return false;
    }

    @Override
    public List<VmDTO> getAllVms() {
        return null;
    }

    @Override
    public VmDTO getVm(Long vmId) {
        return null;
    }

    @Override
    public List<VmDTO> getVmsOfGroup(Long groupId) {
        return null;
    }

    @Override
    public List<VmDTO> getVmsOfStudent(String studentId) {
        return null;
    }

    @Override
    public List<VmDTO> getVmsOfCourse(String courseName) {
        return null;
    }

    @Override
    public List<VmDTO> getVmsOfStudentOfCourse(String studentId, String courseName) {
        return null;
    }
}
