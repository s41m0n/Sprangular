package it.polito.ai.lab2.services;

import it.polito.ai.lab2.entities.Professor;
import it.polito.ai.lab2.entities.Student;
import it.polito.ai.lab2.entities.Vm;
import it.polito.ai.lab2.repositories.CourseRepository;
import it.polito.ai.lab2.repositories.ProfessorRepository;
import it.polito.ai.lab2.repositories.StudentRepository;
import it.polito.ai.lab2.repositories.VmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SecurityServiceImpl implements SecurityService{

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    ProfessorRepository professorRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    VmRepository vmRepository;

    @Override
    public boolean isStudentSelf(String id) {
        return SecurityContextHolder.getContext().getAuthentication().getName().equals(id);
    }

    @Override
    public boolean isTeamOfStudentCourse(Long id) {
        return studentRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .map(student -> student.getCourses().stream()
                        .anyMatch(course -> course.getTeams().stream()
                                .anyMatch(team -> team.getId().equals(id))))
                .orElse(false);
    }

    @Override
    public boolean isProfessorCourseOwner(String courseId) {

        if(!courseRepository.existsById(courseId) || courseRepository.getOne(courseId).getProfessors().isEmpty()){ //the course does not exists or there are no professors
            return false;
        }

        List<String> professorsIds = new ArrayList<>();

        for(Professor p : courseRepository.getOne(courseId).getProfessors()){
            professorsIds.add(p.getId());
        }

        return professorsIds.contains(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Override
    public boolean isStudentEnrolled(String courseId) {
        return studentRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .map(student -> student.getCourses().stream().anyMatch(c -> c.getAcronym().equals(courseId)))
                .orElse(false);
    }

    @Override
    public boolean isTeamOfProfessorCourse(Long id) {
        return professorRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .map(professor -> professor.getCourses().stream()
                    .anyMatch(course -> course.getTeams().stream()
                        .anyMatch(team -> team.getId().equals(id))))
                .orElse(false);
    }

    @Override
    public boolean isStudentInTeamRequest(List<String> memberIds) {
        return memberIds.contains(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Override
    public boolean isStudentOwnerOfVm(Long vmId, String studentId) {
        Vm vm = vmRepository.findById(vmId).orElse(null);
        Student student = studentRepository.findById(studentId).orElse(null);

        if(vm == null || student == null){
            return false;
        }

        return vm.getOwners().contains(student);
    }
}
