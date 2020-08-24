package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface StudentService {

    Optional<StudentDTO> getStudent(String studentId);

    List<StudentDTO> getAllStudents();

    boolean addStudent(StudentDTO student);

    List<Boolean> addAll(List<StudentDTO> students);

    boolean addStudentToCourse(String studentId, String courseName);

    List<Boolean> enrollAll(List<String> studentIds, String courseName);

    List<Boolean> addAndEnroll(Reader r, String courseName);

    List<CourseDTO> getStudentCourses(String studentId);

    boolean removeStudentFromCourse(String studentId, String courseName);
}
