package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.ProfessorDTO;
import it.polito.ai.lab2.dtos.StudentDTO;

import java.util.List;
import java.util.Optional;

public interface CourseService {

    boolean addCourse(CourseDTO course);

    boolean removeCourse(String courseId);

    boolean updateCourse(CourseDTO courseDTO);

    Optional<CourseDTO> getCourse(String courseId);

    List<CourseDTO> getAllCourses();

    List<StudentDTO> getEnrolledStudents(String courseId);

    void enableCourse(String courseId);

    void disableCourse(String courseId);

    List<ProfessorDTO> getCourseProfessors(String courseId);

    boolean addProfessorToCourse(String professorId, String courseId);

    boolean removeProfessorFromCourse(String professorId, String courseId);
}
