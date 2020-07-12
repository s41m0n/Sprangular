package it.polito.ai.lab2.services;

import it.polito.ai.lab2.repositories.CourseRepository;
import it.polito.ai.lab2.repositories.ProfessorRepository;
import it.polito.ai.lab2.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityServiceImpl implements SecurityService{

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    ProfessorRepository professorRepository;

    @Autowired
    CourseRepository courseRepository;

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
    public boolean isProfessorCourseOwner(String course) {
        return courseRepository.findById(course)
                .map(c -> c.getProfessor() != null && c.getProfessor().getId().equals(SecurityContextHolder.getContext().getAuthentication().getName()))
                .orElse(false);
    }

    @Override
    public boolean isStudentEnrolled(String course) {
        return studentRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .map(student -> student.getCourses().stream().anyMatch(c -> c.getName().equals(course)))
                .orElse(false);
    }

    @Override
    public boolean isTeamOfProfessorCourse(Long id) {
        return professorRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .map(professor -> professor.getProfessorCourses().stream()
                    .anyMatch(course -> course.getTeams().stream()
                        .anyMatch(team -> team.getId().equals(id))))
                .orElse(false);
    }

    @Override
    public boolean isStudentInTeamRequest(List<String> memberIds) {
        return memberIds.contains(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
