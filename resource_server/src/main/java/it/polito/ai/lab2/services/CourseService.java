package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.ProfessorDTO;
import it.polito.ai.lab2.dtos.StudentDTO;

import java.util.List;
import java.util.Optional;

public interface CourseService {

    boolean addCourse(CourseDTO course);

    boolean removeCourse(String courseName);

    boolean updateCourse(CourseDTO courseDTO);

    Optional<CourseDTO> getCourse(String courseName);

    List<CourseDTO> getAllCourses();

    List<StudentDTO> getEnrolledStudents(String courseName);

    void enableCourse(String courseName);

    void disableCourse(String courseName);

    List<ProfessorDTO> getCourseProfessors(String courseName);

    boolean addProfessorToCourse(String professorId, String courseName);

    boolean removeProfessorFromCourse(String professorId, String courseName);
}
