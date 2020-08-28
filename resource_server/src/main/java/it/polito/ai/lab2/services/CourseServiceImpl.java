package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.ProfessorDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.exceptions.*;
import it.polito.ai.lab2.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ProfessorRepository professorRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean addCourse(CourseDTO course) {
        if (course.getTeamMaxSize() < course.getTeamMinSize() || courseRepository.findById(course.getName()).isPresent())
            return false;
        Course c = modelMapper.map(course, Course.class);
        courseRepository.save(c);
        return true;
    }

    @Override
    public Optional<CourseDTO> getCourse(String name) {
        return courseRepository.findById(name)
                .map(course -> modelMapper.map(course, CourseDTO.class));
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(course -> modelMapper.map(course, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseName)) or (hasRole('ROLE_STUDENT') and @securityServiceImpl.isStudentEnrolled(#courseName))")
    public List<StudentDTO> getEnrolledStudents(String courseName) {
        return courseRepository.findById(courseName)
                .map(course -> course.getStudents().stream()
                        .map(student -> modelMapper.map(student, StudentDTO.class))
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new CourseNotFoundException("Course " + courseName + " does not exist"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseName))")
    public void enableCourse(String courseName) {
        courseRepository.findById(courseName)
                .ifPresentOrElse(course -> {
                    if (course.getProfessors().isEmpty())
                        throw new CourseProfessorNotAssigned("You can enable course `" + courseName + "` only when at least one professor has been assigned to it");
                    course.setEnabled(true);
                }, () -> {
                    throw new CourseNotFoundException("Course " + courseName + " does not exist");
                });
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PROFESSOR') and @securityServiceImpl.isProfessorCourseOwner(#courseName))")
    public void disableCourse(String courseName) {
        courseRepository.findById(courseName)
                .ifPresentOrElse(course -> course.setEnabled(false), () -> {
                    throw new CourseNotFoundException("Course " + courseName + " does not exist");
                });
    }

    @Override
    public List<ProfessorDTO> getCourseProfessors(String name) {
        Course c = courseRepository.findById(name).orElseThrow(() -> new CourseNotFoundException("Course `" + name + "` does not exist"));
        return c.getProfessors() == null ? null : c.getProfessors().stream().map(p -> modelMapper.map(p, ProfessorDTO.class)).collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean addProfessorToCourse(String professor, String name) {
        Course c = courseRepository.findById(name).orElseThrow(() -> new CourseNotFoundException("Course `" + name + "` does not exist"));
        Professor p = professor.isEmpty() ? null : professorRepository.findById(professor).orElseThrow(() -> new ProfessorNotFoundException("Professor `" + professor + "` does not exist"));
        if (p != null) p.addCourse(c);
        return true;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean removeProfessorFromCourse(String professor, String courseName) {
        Course c = courseRepository.findById(courseName).orElseThrow(() -> new CourseNotFoundException("Course `" + courseName + "` does not exist"));
        Professor p = professor.isEmpty() ? null : professorRepository.findById(professor).orElseThrow(() -> new ProfessorNotFoundException("Professor `" + professor + "` does not exist"));
        if (p != null) p.removeCourse(c);
        return true;
    }

    @Override
    public boolean removeCourse(String courseName) {
        Course c = courseRepository.findById(courseName).orElseThrow(() -> new CourseNotFoundException("Course `" + courseName + "` does not exist"));
        if(c.getStudents().isEmpty()){
            throw new CourseNotEmptyException("Course " + courseName + " has one or more students enrolled, you cannot delete it");
        }
        courseRepository.delete(c);
        return true;
    }

    //TODO: check che il DTO sia pieno
    @Override
    public boolean updateCourse(CourseDTO courseDTO) {
        if (courseDTO.getTeamMaxSize() < courseDTO.getTeamMinSize()) {
            return false;
        }
        Course c = courseRepository.findById(courseDTO.getName()).orElseThrow(() -> new CourseNotFoundException("Course `" + courseDTO.getName() + "` does not exist"));
        c.setName(courseDTO.getName());
        c.setAcronym(courseDTO.getAcronym());
        c.setTeamMaxSize(courseDTO.getTeamMaxSize());
        c.setTeamMinSize(courseDTO.getTeamMinSize());
        c.setEnabled(courseDTO.isEnabled());
        courseRepository.save(c);
        return true;
    }
}
