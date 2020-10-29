package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.ProfessorDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.pojos.CourseWithModelDetails;
import it.polito.ai.lab2.pojos.StudentWithTeamDetails;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface CourseService {

  CourseDTO addCourse(CourseWithModelDetails course);

  CourseDTO removeCourse(String courseId);

  CourseDTO updateCourse(String courseId, CourseWithModelDetails course);

  Optional<CourseDTO> getCourse(String courseId);

  List<CourseDTO> getAllCourses();

  List<StudentWithTeamDetails> getEnrolledStudents(String courseId);

  boolean enableCourse(String courseId);

  boolean disableCourse(String courseId);

  boolean addStudentToCourse(String studentId, String courseId);

  List<Boolean> enrollAll(Reader r, String courseID);

  StudentDTO removeStudentFromCourse(String studentId, String courseId);

  List<ProfessorDTO> getCourseProfessors(String courseId);

  boolean addProfessorToCourse(String professorId, String courseId);

  ProfessorDTO removeProfessorFromCourse(String professorId, String courseId);

  List<StudentWithTeamDetails> getStudentsOfCourse(String courseId, String pattern);
}
