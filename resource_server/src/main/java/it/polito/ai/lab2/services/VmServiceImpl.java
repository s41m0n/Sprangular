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

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public boolean createVmModel(VmModelDTO vmModelDTO, String courseId) {
        Course course = courseRepository.findByName(courseId).orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));
        if(course.getVmModel() != null){
            return false;
        }
        VmModel v = new VmModel();
        v.setName(vmModelDTO.getName());
        v.setImagePath(vmModelDTO.getImagePath());
        v.setCourse(course);
        vmModelRepository.save(v);
        return true;
    }

    @Override
    public VmDTO createVm(Long teamId, VmDTO vmDTO) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException("Team " + teamId + " does not exist"));
        Student owner = studentRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new StudentNotFoundException("Student " + SecurityContextHolder.getContext().getAuthentication().getName() + " does not exist"));

        if (!owner.getCourses().contains(team.getCourse())) { //student not enrolled in course
            throw new StudentNotInCourseException("Student " + owner.getId() + " is not enrolled in course " + team.getCourse().getAcronym());
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

        if (actualVCpu + vmDTO.getVCpu() > team.getMaxVCpu() || actualRam + vmDTO.getRam() > team.getMaxRam() || actualDiskStorage + vmDTO.getDiskStorage() > team.getMaxDiskStorage()) {
            throw new MaxVmResourcesException("Cannot create the VM, no more resources available");
        }

        if (vmDTO.isActive() && numOfActiveVms + 1 > team.getMaxActiveInstances()) {
            throw new MaxVmResourcesException("Cannot create the VM, no more resources available");
        }

        Vm vm = new Vm();
        vm.setVmModel(team.getCourse().getVmModel());
        vm.setActive(vmDTO.isActive());
        vm.setVCpu(vmDTO.getVCpu());
        vm.setRam(vmDTO.getRam());
        vm.setDiskStorage(vmDTO.getDiskStorage());
        vm.setImagePath(vmDTO.getImagePath());
        vm.setTeam(team);
        vm.addOwner(owner);
        vmRepository.save(vm);
        return modelMapper.map(vm, VmDTO.class);
    }

    @Override
    public VmDTO deleteVm(Long vmId, Long teamId) {
        Vm vm = vmRepository.findById(vmId).orElseThrow(() -> new VmNotFoundException("Vm " + vmId + " does not exist"));

        if(!vm.getTeam().getId().equals(teamId)){
            throw new VmNotOfTeamException("Vm " + vmId + " does not belong to team " + teamId);
        }

        if (vm.getOwners().contains(studentRepository.getOne(SecurityContextHolder.getContext().getAuthentication().getName())) && !vm.isActive()) {
            vmRepository.delete(vm);
            return modelMapper.map(vm, VmDTO.class);
        }
        throw new CannotDeleteVmException("VM " + vmId + " cannot be deleted. VM is still active or you are not one of the owners");
    }

    @Override
    public VmDTO updateVmResources(Long vmId, Long teamId, int vCpu, int diskStorage, int ram) { //only owners, TODO: implementa check con il principal
        Vm vm = vmRepository.findById(vmId).orElseThrow(() -> new VmNotFoundException("Vm " + vmId + " does not exist"));

        if (vm.isActive()) {
            throw new VmIsActiveException("Vm " + vmId + " is active and cannot be updated. Please turn it off");
        }

        if(!vm.getTeam().getId().equals(teamId)){
            throw new VmNotOfTeamException("Vm " + vmId + " does not belong to team " + teamId);
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

        if (actualVCpu - vm.getVCpu() + vCpu > team.getMaxVCpu() || actualRam - vm.getRam() + ram > team.getMaxRam() || actualDiskStorage - vm.getDiskStorage() + diskStorage > team.getMaxDiskStorage()) {
            throw new MaxVmResourcesException("Cannot create the VM, no more resources available");
        }

        vm.setVCpu(vCpu);
        vm.setRam(ram);
        vm.setDiskStorage(diskStorage);
        vmRepository.save(vm);
        return modelMapper.map(vm, VmDTO.class);
    }

    @Override
    public VmDTO turnOnVm(Long vmId, Long teamId) { //only owners
        Vm vm = vmRepository.findById(vmId).orElseThrow(() -> new VmNotFoundException("Vm " + vmId + " does not exist"));
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException("Team " + teamId + " does not exist"));

        if(!vm.getTeam().getId().equals(teamId)){
            throw new VmNotOfTeamException("Vm " + vmId + " does not belong to team " + teamId);
        }

        int numOfActiveVms = 0;

        for (Vm v : team.getVms()) {
            if (v.isActive()) {
                numOfActiveVms += 1;
            }
        }

        if(numOfActiveVms + 1 > team.getMaxActiveInstances()){
            throw new MaxVmResourcesException("Cannot turn on the VM, too many active VMs");
        }

        vm.setActive(true);
        vmRepository.save(vm);
        return modelMapper.map(vm, VmDTO.class);
    }

    @Override
    public VmDTO turnOffVm(Long vmId, Long teamId) { //only owners
        Vm vm = vmRepository.findById(vmId).orElseThrow(() -> new VmNotFoundException("Vm " + vmId + " does not exist"));
        teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException("Team " + teamId + " does not exist"));

        if(!vm.getTeam().getId().equals(teamId)){
            throw new VmNotOfTeamException("Vm " + vmId + " does not belong to team " + teamId);
        }

        vm.setActive(false);
        vmRepository.save(vm);
        return modelMapper.map(vm, VmDTO.class);
    }

    @Override
    public void addVmOwner(Long vmId, Long teamId, String studentId) { //only owners
        Vm vm = vmRepository.findById(vmId).orElseThrow(() -> new VmNotFoundException("Vm " + vmId + " does not exist"));
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new StudentNotFoundException("Student " + studentId + " does not exist"));
        teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException("Team " + teamId + " does not exist"));

        if(!vm.getTeam().getId().equals(teamId)){
            throw new VmNotOfTeamException("Vm " + vmId + " does not belong to team " + teamId);
        }

        if (!vm.getOwners().contains(student)) {
            vm.addOwner(student);
        }
    }

    @Override
    public VmDTO getVm(Long vmId, Long teamId) {
        Vm vm = vmRepository.findById(vmId).orElseThrow(() -> new VmNotFoundException("Vm " + vmId + " does not exist"));

        if(!vm.getTeam().getId().equals(teamId)){
            throw new VmNotOfTeamException("Vm " + vmId + " does not belong to team " + teamId);
        }

        return modelMapper.map(vm, VmDTO.class);
    }

    @Override
    public List<VmDTO> getVmsOfTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .map(team -> team.getVms().stream()
                        .map(vm -> modelMapper.map(vm, VmDTO.class))
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new TeamNotFoundException("Team " + teamId + " does not exist"));
    }

    @Override
    public List<VmDTO> getVmsOfCourse(String courseId) { //only professor
        Course course = courseRepository.findByName(courseId).orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));
        List<VmDTO> returnedList = new ArrayList<>();

        for (Team t : course.getTeams()) {
            returnedList.addAll(t.getVms().stream()
                    .map(vm -> modelMapper.map(vm, VmDTO.class))
                    .collect(Collectors.toList()));
        }
        return returnedList;
    }

    @Override
    public List<VmDTO> getVmsOfStudentOfCourse(String studentId, String courseId) { //tutte le vm collegate al suo team
        courseRepository.findByName(courseId).orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new StudentNotFoundException("Student " + studentId + " does not exist"));

        Team team = null;
        for (Team t : student.getTeams()) { //find the student's team of the selected course
            if (t.getCourse().getName().equals(courseId)) {
                team = t;
                break;
            }
        }

        if (team == null) {
            throw new StudentNotInTeamOfCourseException("Student " + studentId + "does not belong to a team of course " + courseId);
        }

        return team.getVms().stream()
                .map(vm -> modelMapper.map(vm, VmDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<VmDTO> getOwnedVmsOfStudentOfCourse(String studentId, String courseId) { //solo quelle che possiede
        courseRepository.findByName(courseId).orElseThrow(() -> new CourseNotFoundException("Course " + courseId + " does not exist"));
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new StudentNotFoundException("Student " + studentId + " does not exist"));

        return student.getOwnedVms().stream()
                .filter(vm -> vm.getTeam().getCourse().getName().equals(courseId))
                .map(vm -> modelMapper.map(vm, VmDTO.class))
                .collect(Collectors.toList());
    }
}
