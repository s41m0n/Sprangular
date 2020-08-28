package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.VmDTO;
import it.polito.ai.lab2.dtos.VmModelDTO;
import it.polito.ai.lab2.entities.Course;
import it.polito.ai.lab2.entities.Student;
import it.polito.ai.lab2.entities.Vm;
import it.polito.ai.lab2.entities.VmModel;
import it.polito.ai.lab2.exceptions.CourseNotFoundException;
import it.polito.ai.lab2.exceptions.StudentNotFoundException;
import it.polito.ai.lab2.exceptions.VmModelNotFoundException;
import it.polito.ai.lab2.exceptions.VmNotFoundException;
import it.polito.ai.lab2.repositories.CourseRepository;
import it.polito.ai.lab2.repositories.StudentRepository;
import it.polito.ai.lab2.repositories.VmModelRepository;
import it.polito.ai.lab2.repositories.VmRepository;
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
    public boolean createVm(Long vmModelId, VmDTO vmDTO, String ownerId) {
        VmModel vmModel = vmModelRepository.findById(vmModelId).orElseThrow(() -> new VmModelNotFoundException("VmModel " + vmModelId + " does not exist"));
        Student owner = studentRepository.findById(ownerId).orElseThrow(() -> new StudentNotFoundException("Student " + ownerId + " does not exist"));

        //TODO: manca tutto il controllo delle risorse!

        Vm vm = new Vm();
        vm.setVmModel(vmModel);
        vm.setActive(vmDTO.isActive());
        vm.setVCpu(vmDTO.getVCpu());
        vm.setRam(vmDTO.getRam());
        vm.setDiskStorage(vmDTO.getDiskStorage());
        vm.setImagePath(vmDTO.getImagePath());
        vm.getOwners().add(owner);
        vmRepository.save(vm);
        vmModel.getVms().add(vm);
        return true;
    }

    @Override
    public boolean deleteVm(Long vmId) {
        Vm vm = vmRepository.findById(vmId).orElseThrow(() -> new VmNotFoundException("Vm " + vmId + " does not exist"));

        if(vm.getOwners().contains(studentRepository.getOne(SecurityContextHolder.getContext().getAuthentication().getName()))){
            vmRepository.delete(vm);
            return true;
            //TODO: facciamo che si può eliminare anche una macchina virtuale accesa?
        }
        return false; //TODO: facciamo un'eccezione o lasciamo che torni falso e gli mandiamo giù un messaggio?
    }

    @Override
    public boolean updateVmResourceLimits(Long vmId, int vCpu, int diskStorage, int ram) {
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
