package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.ProfessorDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.pojos.UpdateCourseDetails;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface CourseService {

  boolean addCourse(CourseDTO course);

  CourseDTO removeCourse(String courseId);

  CourseDTO updateCourse(String courseId, UpdateCourseDetails updateCourseDetails);

  Optional<CourseDTO> getCourse(String courseId);

  List<CourseDTO> getAllCourses();

  List<StudentDTO> getEnrolledStudents(String courseId);

  void enableCourse(String courseId);

  void disableCourse(String courseId);

  boolean addStudentToCourse(String studentId, String courseId);

  List<Boolean> enrollAll(Reader r, String courseID);

  StudentDTO removeStudentFromCourse(String studentId, String courseId);

  List<ProfessorDTO> getCourseProfessors(String courseId);

  boolean addProfessorToCourse(String professorId, String courseId);

  ProfessorDTO removeProfessorFromCourse(String professorId, String courseId);

  List<StudentDTO> getStudentsOfCourse(String courseId);

  List<StudentDTO> getStudentsOfCourseLike(String courseId, String pattern);
}
