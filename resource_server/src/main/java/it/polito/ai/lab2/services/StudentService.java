package it.polito.ai.lab2.services;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.pojos.TeamProposalDetails;

import java.util.List;
import java.util.Optional;

public interface StudentService {

  Optional<StudentDTO> getStudent(String studentId);

  List<StudentDTO> getAllStudents();

  List<CourseDTO> getStudentCourses(String studentId);

  List<TeamProposalDetails> getProposalsForStudentOfCourse(String studentId, String courseId);

  List<StudentDTO> getStudentsLike(String pattern);
}
